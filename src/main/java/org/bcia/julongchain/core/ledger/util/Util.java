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
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.TransactionPackage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Ledger工具类
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class Util {
    private static final JavaChainLog logger   = JavaChainLogFactory.getLog(Util.class);
    /**
     * 获取Envelope结构
     */
    public static Common.Envelope getEnvelopFromBlock(ByteString data){
        //block以envelop开始
        Common.Envelope env = null;
        try {
            env = Common.Envelope.parseFrom(data);
            return env;
        } catch (InvalidProtocolBufferException e) {
            logger.error("Got error when getting Envelope");
            return null;
        }
    }

    /**
     * 获取payload
     */
    public static Common.Payload getPayload(Common.Envelope env){
        Common.Payload payload = null;
        try {
            payload = Common.Payload.parseFrom(env.getPayload());
            return payload;
        } catch (Exception e) {
            logger.error("Got error when getting Payload");
            return null;
        }
    }

    /**
     * 获取GroupHeader
     */
    public static Common.GroupHeader getGroupHeader(ByteString data){
        Common.GroupHeader header = null;
        try {
            header = Common.GroupHeader.parseFrom(data);
            return header;
        } catch (InvalidProtocolBufferException e) {
            logger.error("Got error when getting GroupHeader");
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
            logger.error("Got error when getting Transaction");
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
            logger.error("Got error when getting SmartContractActionPayload");
            logger.error(e.getMessage(), e);
            return null;
        }
        if(scaPayload.getAction() == null || scaPayload.getAction().getProposalResponsePayload() == null){
            logger.error("No valid payload in SmartContractActionPayload");
            return null;
        }
        try {
            prPayload = ProposalResponsePackage.ProposalResponsePayload.parseFrom(scaPayload.getAction().getProposalResponsePayload());
        } catch (InvalidProtocolBufferException e) {
            logger.error("Got error when getting ProposalResponsePayload");
            logger.error(e.getMessage(), e);
            return null;
        }
        if(prPayload.getExtension() == null){
            logger.error("Response payload missed extension");
            return null;
        }
        try {
            respPayload = ProposalPackage.SmartContractAction.parseFrom(prPayload.getExtension());
        } catch (InvalidProtocolBufferException e) {
            logger.error("Got error when getting SmartContractAction");
            logger.error(e.getMessage(), e);
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
                target = CspManager.getDefaultCsp().hash(bytes, null);
            } catch (JavaChainException e) {
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
     *
     */
    public static <T> List<T> getValuesBySortedKeys (Map<String, T> m){
        List<String> list = getSortedKeys(m);
        List<T> l = new ArrayList<>();
        for(String s : list){
            l.add(m.get(s));
        }
        return l;
    }
}
