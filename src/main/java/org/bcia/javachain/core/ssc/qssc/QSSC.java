/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.core.ssc.qssc;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.aclmgmt.AclManagement;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.node.util.NodeUtils;
import org.bcia.javachain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.TransactionPackage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询系统智能合约　Query System Smart Contract,CSSC
 * CSSC implements the ledger query functions, including:
 * -GetChainInfo returns BlockchainInfo
 * -GetBlockByNumber returns a block
 * -GetBlockByHash returns a block
 * -GetTransactionByID returns a transaction
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */
@Component
public class QSSC extends SystemSmartContractBase {
    private static JavaChainLog log = JavaChainLogFactory.getLog(QSSC.class);
    // These are function names from Invoke first parameter
    public final static String GET_GROUP_INFO="GetGroupInfo";

    public final static String GET_BLOCK_BY_NUMBER="GetBlockByNumber";

    public final static String GET_BLOCK_BY_HASH="GetBlockByHash";

    public final static String GET_TRANSACTION_BY_ID="GetTransactionByID";

    public final static String GET_BLOCK_BY_TX_ID="GetBlockByTxID";

    // Init is called once per chain when the chain is created.
    // This allows the chaincode to initialize any variables on the ledger prior
    // to any transaction execution on the chain.
    @Override
    public SmartContractResponse init(ISmartContractStub stub) {
        log.info("Successfully initialized QSSC");
        return newSuccessResponse();
    }

    // Invoke is called with args[0] contains the query function name, args[1]
    // contains the chain ID, which is temporary for now until it is part of stub.
    // Each function requires additional parameters as described below:
    // # GetChainInfo: Return a BlockchainInfo object marshalled in bytes
    // # GetBlockByNumber: Return the block specified by block number in args[2]
    // # GetBlockByHash: Return the block specified by block hash in args[2]
    // # GetTransactionByID: Return the transaction specified by ID in args[2]
    @Override
    public SmartContractResponse invoke(ISmartContractStub stub) {
        log.debug("Enter QSSC invoke function");
        List<byte[]> args = stub.getArgs();
        int size=args.size();
        if(size<2){
            return newErrorResponse(String.format("Incorrect number of arguments, %d)",args.size()));
        }
        String function= ByteString.copyFrom(args.get(0)).toStringUtf8();
        String groupID=ByteString.copyFrom(args.get(1)).toStringUtf8();
        if(function!=GET_GROUP_INFO && size<3){
            return newErrorResponse(String.format("Missing 3rd argument for %s",function));
        }
        INodeLedger targetLedger = NodeUtils.getLedger(groupID);
        if(targetLedger==null){
            return newErrorResponse(String.format("Invalid group ID %s",groupID));
        }
        log.debug("Invoke function:{} on group:{}",function,groupID);

        // Handle ACL:
        // 1. get the signed proposal
        ProposalPackage.SignedProposal sp=stub.getSignedProposal();

        // 2. check the channel reader policy
        String res=getACLResource(function);
        try{
            AclManagement.getACLProvider().checkACL(function,groupID,sp);
        }catch(JavaChainException e){
            newErrorResponse(String.format("Authorization request for [%s][%s] failed:%s",function,groupID,e.getMessage()));
        }

        byte[] arg2=args.get(2);

        switch(function){
            case GET_TRANSACTION_BY_ID:
                return getTransactionByID(targetLedger,arg2);
            case GET_BLOCK_BY_NUMBER:
                return getBlockByNumber(targetLedger,arg2);
            case GET_BLOCK_BY_HASH:
                return getBlockByHash(targetLedger,arg2);
            case GET_GROUP_INFO:
                return getChainInfo(targetLedger);
            case GET_BLOCK_BY_TX_ID:
                return getBlockByTxID(targetLedger,arg2);
            default:
                return newErrorResponse(String.format("Request function %s not found",function));
        }
    }

    @Override
    public String getSmartContractStrDescription() {
        String description="与查询相关的系统智能合约";
        return description;
    }

    private String getACLResource(String function){
        return "QSSC."+function;
    }

    /**
     * 获取交易数据
     * @param vledger
     * @param tid
     * @return
     */
    private SmartContractResponse getTransactionByID(INodeLedger vledger, byte[] tid){
        if(tid==null || tid.length==0){
            return newErrorResponse("Transaction ID must not be nil.");
        }
        String txID=ByteString.copyFrom(tid).toStringUtf8();
        TransactionPackage.ProcessedTransaction processedTran;
        try {
            processedTran=vledger.getTransactionByID(txID);
        } catch (LedgerException e) {
            String msg=String.format("Failed to get transaction with id %s, error %s",txID,e.getMessage());
            return newErrorResponse(msg);
        }
        byte[] tranBytes = processedTran.toByteArray();
        return newSuccessResponse(tranBytes);
    }

    /**
     * 获取块
     * @param vledger
     * @param number
     * @return
     */
    private SmartContractResponse getBlockByNumber(INodeLedger vledger, byte[] number){
        if(number==null ||number.length==0){
            return newErrorResponse("Block number must not be nil.");
        }
        long bnum=0;
        try {
            bnum = Long.parseLong(ByteString.copyFrom(number).toString());
        }catch(Exception e){
            String msg=String.format("Failed to parse block number with error %s",e.getMessage());
            return newErrorResponse(msg);
        }
        Common.Block block =null;
        try {
            block=vledger.getBlockByNumber(bnum);
        } catch (LedgerException e) {
            String msg=String.format("Failed to get block number %d, error %s",bnum,e.getMessage());
            return newErrorResponse(msg);
        }

        // TODO: consider trim block content before returning
        //  Specifically, trim transaction 'data' out of the transaction array Payloads
        //  This will preserve the transaction Payload header,
        //  and client can do GetTransactionByID() if they want the full transaction details
        byte[] blockBytes = block.toByteArray();
        return newSuccessResponse(blockBytes);
    }

    /**
     * 根据hash获取块
     * @param vledger
     * @param hash
     * @return
     */
    private SmartContractResponse getBlockByHash(INodeLedger vledger, byte[] hash){
        if(hash==null ||hash.length==0){
            return newErrorResponse("Block hash must not be nil.");
        }
        Common.Block block =null;
        try {
            block=vledger.getBlockByHash(hash);
        } catch (LedgerException e) {
            String msg=String.format("Failed to get block hash %s,error %s",ByteString.copyFrom(hash).toString(),e.getMessage());
            return newErrorResponse(msg);
        }
        byte[] bytes = block.toByteArray();
        return newSuccessResponse(bytes);
    }

    /**
     * 获取区块链信息
     * @param vledger
     * @return
     */
    private SmartContractResponse getChainInfo(INodeLedger vledger){
        Ledger.BlockchainInfo bcInfo=null;
        try {
            bcInfo = vledger.getBlockchainInfo();
        } catch (LedgerException e) {
            String msg=String.format("Failed to get block info with error %s",e.getMessage());
            return newErrorResponse(msg);
        }
        byte[] bytes = bcInfo.toByteArray();
        return newSuccessResponse(bytes);
    }

    /**
     * 根据交易ID获取块
     * @param vledger
     * @param rawTxID
     * @return
     */
    private SmartContractResponse getBlockByTxID(INodeLedger vledger, byte[] rawTxID){
        String txID=ByteString.copyFrom(rawTxID).toString();
        Common.Block block =null;
        try {
            block=vledger.getBlockByTxID(txID);
        } catch (LedgerException e) {
            String msg=String.format("Failed to get block for txID %s,error %s",txID,e.getMessage());
            return newErrorResponse(msg);
        }
        byte[] bytes = block.toByteArray();
        return newSuccessResponse(bytes);
    }


}