/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.core.ledger.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.BlockFileStream;
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.MerkleTree;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.csp.gm.dxct.sm3.SM3HashOpts;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.TransactionPackage;
import org.bouncycastle.util.encoders.Hex;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Ledger工具类
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class Util {
    private static JulongChainLog log = JulongChainLogFactory.getLog("Ledger");
    /**
     * 获取Envelope结构
     */
    public static Common.Envelope getEnvelopFromBlock(ByteString data){
        //block以envelop开始
        Common.Envelope env;
        try {
            env = Common.Envelope.parseFrom(data);
            return env;
        } catch (InvalidProtocolBufferException e) {
            log.error("Got error when getting Envelope");
            return null;
        }
    }

    /**
     * 获取payload
     */
    public static Common.Payload getPayload(Common.Envelope env){
        Common.Payload payload;
        try {
            payload = Common.Payload.parseFrom(env.getPayload());
            return payload;
        } catch (Exception e) {
            log.error("Got error when getting Payload");
            return null;
        }
    }

    /**
     * 获取GroupHeader
     */
    public static Common.GroupHeader getGroupHeader(ByteString data){
        Common.GroupHeader header;
        try {
            header = Common.GroupHeader.parseFrom(data);
            return header;
        } catch (InvalidProtocolBufferException e) {
            log.error("Got error when getting GroupHeader");
            return null;
        }
    }

    /**
     * 获取Transaction
     */
    public static TransactionPackage.Transaction getTransaction(ByteString txBytes){
        TransactionPackage.Transaction tx = null;
        try {
            tx = TransactionPackage.Transaction.parseFrom(txBytes);
            return tx;
        } catch (InvalidProtocolBufferException e) {
            log.error("Got error when getting Transaction");
            return null;
        }
    }

    /**
     * 获取Type为ENDORSER_TRANSACTION的payload
     */
    public static ProposalPackage.SmartContractAction getPayloads(TransactionPackage.TransactionAction txAction){
        TransactionPackage.SmartContractActionPayload scaPayload = null;
        ProposalResponsePackage.ProposalResponsePayload prPayload = null;
        ProposalPackage.SmartContractAction respPayload = null;
        try {
            scaPayload = TransactionPackage.SmartContractActionPayload.parseFrom(txAction.getPayload());
        } catch (InvalidProtocolBufferException e) {
            log.error("Got error when getting SmartContractActionPayload");
            log.error(e.getMessage(), e);
            return null;
        }
        if(scaPayload.getAction() == null || scaPayload.getAction().getProposalResponsePayload() == null){
            log.error("No valid payload in SmartContractActionPayload");
            return null;
        }
        try {
            prPayload = ProposalResponsePackage.ProposalResponsePayload.parseFrom(scaPayload.getAction().getProposalResponsePayload());
        } catch (InvalidProtocolBufferException e) {
            log.error("Got error when getting ProposalResponsePayload");
            log.error(e.getMessage(), e);
            return null;
        }
        if(prPayload.getExtension() == null){
            log.error("Response payload missed extension");
            return null;
        }
        try {
            respPayload = ProposalPackage.SmartContractAction.parseFrom(prPayload.getExtension());
        } catch (InvalidProtocolBufferException e) {
            log.error("Got error when getting SmartContractAction");
            log.error(e.getMessage(), e);
            return null;
        }
        return respPayload;
    }


    /**
     * 在Envelope中获取Action
     */
    public static ProposalPackage.SmartContractAction getActionFromEnvelope(ByteString envBytes){
        Common.Envelope env = getEnvelopFromBlock(envBytes);
        Common.Payload payload = getPayload(env);
        ProposalPackage.SmartContractAction respPayload = null;
        TransactionPackage.Transaction tx = getTransaction(payload.getData());
        if(tx.getActionsList().size() == 0){
            return null;
        }
        respPayload = getPayloads(tx.getActions(0));
        return respPayload;
    }

    /**
     * byte数组转long
     */
    public static long bytesToLong(byte[] bytes, int start, int length){
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes, start, length).flip();
        return buffer.getLong();
    }

    /**
     * long转byte数组
     */
    public static byte[] longToBytes(long longNum, int length){
        ByteBuffer buffer = ByteBuffer.allocate(length);
        return buffer.putLong(longNum).array();
    }

    /**
     * 进行hash运算
     */
    public static byte[] getHashBytes(byte[] bytes) throws LedgerException {
        byte[] target = null;
        if (bytes != null) {
            try {
                target = CspManager.getDefaultCsp().hash(bytes, new SM3HashOpts());
            } catch (JulongChainException e) {
                throw new LedgerException(e);
            }
        }
        return target;
    }


    /**
     * 获取map key的排序
     */
    public static <T> List<String> getSortedKeys(Map<String, T> m){
        List<String> list = new ArrayList<>();
        for(String key : m.keySet()){
            list.add(key);
        }
        Collections.sort(list);
        return list;
    }

    /**
     * 获取排序后的value
     */
    public static <T> List<T> getValuesBySortedKeys (Map<String, T> m){
        List<String> list = getSortedKeys(m);
        List<T> l = new ArrayList<>();
        for(String s : list){
            l.add(m.get(s));
        }
        return l;
    }

	/**
	 * 解码世界状态value
	 */
	public static byte[] decodeValueToBytes(byte[] encodeValue){
		byte[] result = new byte[encodeValue.length - 16];
		System.arraycopy(encodeValue, 16, result, 0, result.length);
		return result;
	}

	/**
	 * 编码世界状态value
	 */
	public static byte[] encodeValue(byte[] value, LedgerHeight version){
		byte[] encodeValue = version.toBytes();
		if(value != null){
			encodeValue = ArrayUtils.addAll(encodeValue, value);
		}
		return encodeValue;
	}

	/**
	 * 对区块链文件系统
	 */
	public static Set<String> checkChainsDir(String dir) throws LedgerException {
		boolean isChainsDir = false;
		Set<String> chainsRootPath = new HashSet<>();
		File checkDir = new File(dir);
		if (!checkDir.exists() || !checkDir.isDirectory()) {
			log.info("Dir " + dir + " is not an exists dir");
			return chainsRootPath;
		}
		Map<String, File> files = IoUtil.getFileRelativePath(dir);
		for (Map.Entry<String, File> entry : files.entrySet()) {
			String fileRelativePath = entry.getKey();
			File file = entry.getValue();
			String fileName = file.getName();
			isChainsDir = Pattern.matches("^blockfile_[0-9A-Fa-f]{6}$", fileName) &&
					StringUtils.countMatches(fileRelativePath, File.separator) == 1;
			break;
		}
		if (isChainsDir) {
			for (Map.Entry<String, File> entry : files.entrySet()) {
				File file = entry.getValue();
				chainsRootPath.add(file.getParentFile().getAbsolutePath());
				String fileName = file.getName();
				if (!Pattern.matches("^blockfile_[0-9A-Fa-f]{6}$", fileName)) {
					log.info("File [" + file.getAbsolutePath() + "] is not a block chain file, please check");
				}
			}
			log.info("Dir [" + dir + "] is a chains dir");
		} else {
			log.info("Dir [" + dir + "] is not a chains dir");
		}
		return chainsRootPath;
	}

	/**
	 * 群组区块检查，检查群组区块链文件夹下的区块链文件
	 * 根据区块链区块构建原则对区块链文件进行检查
	 * 检查失败将会停止程序，等待查验原因
	 */
	public static void checkGroupBlockFiles(String groupDir) throws LedgerException{
		BlockFileStream stream = new BlockFileStream(groupDir, 0, 0);
		//前一区块头部hash
		MerkleTree headerTree = new MerkleTree(32);
		//当前区块previous hash
		MerkleTree previousHashTree = new MerkleTree(32);
		//区块data hash
		MerkleTree dataHashTree = new MerkleTree(32);
		//区块头部data hash
		MerkleTree dataHashInHeaderTree = new MerkleTree(32);
		Common.Block block = null;
		long totalBlockNum = 0;
		try {
			while (true) {
				byte[] blockBytes = stream.nextBlockBytes();
				if (blockBytes != null) {
					//读取block
					block = Common.Block.parseFrom(blockBytes);
					//更新区块头部hash
					headerTree.update(Util.getHashBytes(block.getHeader().toByteArray()));
					//更新区块previousHash
					if (totalBlockNum > 0) {
						previousHashTree.update(block.getHeader().getPreviousHash().toByteArray());
					}
					//更新区块data hash
					dataHashTree.update(Util.getHashBytes(block.getData().toByteArray()));
					//更新区块头部中data hash
					dataHashInHeaderTree.update(block.getHeader().getDataHash().toByteArray());
					totalBlockNum++;
				} else {
					//由于不存在以最后一个区块header hash为previous hash的区块，所以直接将最后一个区块的header hash加入
					if (block != null) {
						previousHashTree.update(Util.getHashBytes(block.getHeader().toByteArray()));
					}
					break;
				}
			}
			headerTree.done();
			previousHashTree.done();
			dataHashTree.done();
			dataHashInHeaderTree.done();
			log.info("Finished block file check, using MerkleTree");
			String headerTreeStr = Hex.toHexString(headerTree.getRootHash());
			log.info("Summary of block header hash is " + headerTreeStr);
			String previousHashTreeStr = Hex.toHexString(previousHashTree.getRootHash());
			log.info("Summary of block previous hash in block header is " + previousHashTreeStr);
			String dataHashTreeStr = Hex.toHexString(dataHashTree.getRootHash());
			log.info("Summary of block data hash is " + dataHashTreeStr);
			String dataHashInHeaderTreeStr = Hex.toHexString(dataHashInHeaderTree.getRootHash());
			log.info("Summary of block data hash in block header is " + dataHashInHeaderTreeStr);
			if (!headerTreeStr.equals(previousHashTreeStr) || !dataHashTreeStr.equals(dataHashInHeaderTreeStr)) {
				log.error("Block file checking failed");
				retrievalBlockFiles(groupDir);
			} else {
				log.info("Block file in [" + groupDir + "] checking success");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new LedgerException(e);
		}
	}

	/**
	 * 区块链文件检查
	 * 对区块链文件hash完整性进行检查
	 */
	public static void checkBlockFiles(String dir) throws LedgerException{
		Set<String> groupDirs = checkChainsDir(dir);
		for (String groupDir : groupDirs) {
			checkGroupBlockFiles(groupDir);
			log.info("Dir " + groupDir + " passed");
		}
	}

	public static void retrievalBlockFiles(String chainsDir) throws LedgerException{
		try {
			BlockFileStream stream = new BlockFileStream(chainsDir, 0, 0);
			byte[] blockBytes = stream.nextBlockBytes();
			Common.Block preBlock = null;
			Common.Block currentBlock = blockBytes == null ?
					null :
					Common.Block.parseFrom(blockBytes);
			while (true) {
				if (blockBytes == null) {
					log.info("Check block chain files success");
					break;
				}
				//验证preHash和前一区块的header hash
				if (preBlock != null) {
					byte[] preHash = Util.getHashBytes(preBlock.getHeader().toByteArray());
					byte[] preHashInFollowedBlock = currentBlock.getHeader().getPreviousHash().toByteArray();
					if (!Arrays.equals(preHash, preHashInFollowedBlock)) {
						String errMsg = "Block" + currentBlock.getHeader().getNumber() + "'s preHash [" +
								Hex.toHexString(preHashInFollowedBlock) + "] is not the same as Block" +
								preBlock.getHeader().getNumber() + "'s header hash [" +
								Hex.toHexString(preHash) + "]";
						log.error(errMsg);
						log.error("Check block chain files failed");
						// TODO: 9/5/18 exit code undefine
						System.exit(1);
					}
				}
				byte[] dataHash = Util.getHashBytes(currentBlock.getData().toByteArray());
				byte[] dataHashInHeader = currentBlock.getHeader().getDataHash().toByteArray();
				//验证data hash和header中的dataHash
				if (!Arrays.equals(dataHash, dataHashInHeader)) {
					String errMsg = "Block" + currentBlock.getHeader().getNumber() + "'s data hash in header is [" +
							Hex.toHexString(dataHashInHeader) + "] which is not same as data's hash [" +
							Hex.toHexString(dataHash) + "]";
					log.error(errMsg);
					log.error("Check block chain files failed");
					// TODO: 9/5/18 exit code undefine
					System.exit(2);
				}
				blockBytes = stream.nextBlockBytes();
				if (blockBytes == null) {
					log.info("Check block chain files success");
					break;
				} else {
					preBlock = currentBlock;
					currentBlock = Common.Block.parseFrom(blockBytes);
				}
			}
		} catch (InvalidProtocolBufferException e) {
			log.error(e.getMessage(), e);
			log.error("Check block chain files failed");
			// TODO: 9/5/18 exit code undefine
			System.exit(3);
		}
	}
}
