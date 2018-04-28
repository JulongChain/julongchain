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
package org.bcia.javachain.core.ssc.vssc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.SysSmartContractException;
import org.bcia.javachain.common.groupconfig.capability.IApplicationCapabilities;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.RwSetUtil;
import org.bcia.javachain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.javachain.core.ssc.lssc.LSSC;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.ledger.rwset.Rwset;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.protos.node.TransactionPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证对LSSC的调用而设立的支持类
 *
 * @author sunianle
 * @date 4/28/18
 * @company Dingxuan
 */
public class VSSCSupportForLsscInvocation {
    public static void validateLSSCInvocation(
            ISmartContractStub stub,
            String groupID,
            Common.Envelope envelope,
            TransactionPackage.SmartContractActionPayload scap,
            Common.Payload payload,
            IApplicationCapabilities ac,
            JavaChainLog log
    )throws SysSmartContractException{
        ProposalPackage.SmartContractProposalPayload scpp=null;
        try {
            scpp=ProposalPackage.SmartContractProposalPayload.parseFrom(scap.getSmartContractProposalPayload());
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("VSSC error: GetSmartContractProposalPayload failed, err %s",e.getMessage());
            throw new SysSmartContractException(msg);
        }
        Smartcontract.SmartContractInvocationSpec scis=null;
        try {
            Smartcontract.SmartContractInvocationSpec.parseFrom(scpp.getInput());
        } catch (InvalidProtocolBufferException e) {
            String msg=String.format("VSSC error: Unmarshal SmartContractInvocationSpec failed, err %s",e.getMessage());
            throw new SysSmartContractException(msg);
        }

        if(scis.getSmartContractSpec()==null ||
                scis.getSmartContractSpec().getInput()==null ||
                scis.getSmartContractSpec().getInput().getArgsList()==null){
            log.error("VSSC error: committing invalid vssc invocation");
            throw new SysSmartContractException("VSSC error: committing invalid vssc invocation");
        }

        String lsscFunc=scis.getSmartContractSpec().getInput().getArgs(0).toString();
        List<ByteString> argsList = scis.getSmartContractSpec().getInput().getArgsList();
        //去除函数的纯参数列表
        List<ByteString> argsListWithoutFunction=new ArrayList<ByteString>();
        int num=argsList.size();
        for(int i=1;i<num;i++){
            argsListWithoutFunction.add(argsList.get(i));
        }
        log.debug("VSSC info: ValidateLSSCInvocation acting on %s %s",lsscFunc,argsListWithoutFunction.toString());

        switch (lsscFunc){
            case LSSC.UPGRADE:
                ;
            case LSSC.DEPLOY:
                log.debug("VSSC info: validating invocation of lssc function %s on arguments %s",lsscFunc,argsListWithoutFunction.toString());
                int size=argsListWithoutFunction.size();
                if(size<2){
                    String msg=String.format("Wrong number of arguments for invocation lssc(%s): expected at least 2, received %d",lsscFunc,size);
                    throw new SysSmartContractException(msg);
                }
                if((ac.privateGroupData()==false && size>5  ||
                        (ac.privateGroupData()==true) && size>6 )){
                    String msg=String.format("Wrong number of arguments for invocation lssc(%s): expected at least 2, received %d",lsscFunc,size);
                    throw new SysSmartContractException(msg);
                }
                Smartcontract.SmartContractDeploymentSpec scds;
                try {
                    scds=Smartcontract.SmartContractDeploymentSpec.parseFrom(argsListWithoutFunction.get(1));
                } catch (InvalidProtocolBufferException e) {
                    String msg=String.format("GetSmartContractDeploymentSpec error %s",e.getMessage());
                    throw new SysSmartContractException(msg);
                }

                if(scds==null || scds.getSmartContractSpec()==null || scds.getSmartContractSpec().getSmartContractId()==null||
                        scap.getAction()==null || scap.getAction().getProposalResponsePayload()==null){
                    String msg=String.format("VSSC error: invocation of lssc(%s) does not have appropriate arguments",lsscFunc);
                    throw new SysSmartContractException(msg);
                }

                //get the rwset
                ProposalResponsePackage.ProposalResponsePayload pRespPayload =null;
                try {
                    pRespPayload =ProposalResponsePackage.ProposalResponsePayload.parseFrom(scap.getAction().getProposalResponsePayload());
                } catch (InvalidProtocolBufferException e) {
                    String msg=String.format("GetProposalResponsePayload error %s",e.getMessage());
                    throw new SysSmartContractException(msg);
                }
                if(pRespPayload.getExtension()==null){
                    throw new SysSmartContractException("null pRespPayload.Extension");
                }

                ProposalPackage.SmartContractAction respPayload = null;
                try {
                    respPayload=ProposalPackage.SmartContractAction.parseFrom(pRespPayload.getExtension());
                } catch (InvalidProtocolBufferException e) {
                    throw new SysSmartContractException(String.format("GetSmartContractAction error %s",e.getMessage()));
                }
                Rwset.TxReadWriteSet rwSet=null;
                try {
                    rwSet=Rwset.TxReadWriteSet.parseFrom(respPayload.getResults());
                } catch (InvalidProtocolBufferException e) {
                    throw new SysSmartContractException(e.getMessage());
                }
                //RwSetUtil.txRwSetFromProtoMsg

        }
    }
}
