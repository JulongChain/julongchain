/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.javachain.consenter.common.server;


import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.ledger.blockledger.IFactory;
import org.bcia.javachain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.proto.BlockUtils;
import org.bcia.javachain.consenter.common.bootstrap.file.BootStrapHelper;
import org.bcia.javachain.consenter.common.localconfig.ConsenterConfig;
import org.bcia.javachain.consenter.common.multigroup.Registrar;
import org.bcia.javachain.consenter.consensus.IConsensue;
import org.bcia.javachain.consenter.consensus.singleton.Singleton;
import org.bcia.javachain.protos.common.Common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/5/29
 * @company Dingxuan
 */
public class PreStart {
    private static JavaChainLog log = JavaChainLogFactory.getLog(PreStart.class);
    public Registrar initializeMultichannelRegistrar(ConsenterConfig consenterConfig, ILocalSigner signer) throws LedgerException, ValidateException, PolicyException, InvalidProtocolBufferException {
        IFactory lf= LedgerHelper.createLedgerFactroy(consenterConfig);
        if(lf.groupIDs().size()==0){
            initBootstrapGroup(consenterConfig,lf);
        }else {
            log.info("Not bootstrapping because of existing chains");
        }
        Map<String,IConsensue> consenters=new HashMap<>();
        consenters.put("single",new Singleton());
        return new Registrar().newRegistrar(lf,consenters,signer);
    }
    private static void initBootstrapGroup(ConsenterConfig consenterConfig, IFactory blockLedger){
        Common.Block genesisBlock = null;
        switch (consenterConfig.getGeneral().getGenesisMethod()){
            case "provisional":
                //TODO 获取
                return;
            case "file":
                genesisBlock= new BootStrapHelper(consenterConfig.getGeneral().getGenesisFile()).genesisBlock();
            default:
        }
        try {
            String chainId= BlockUtils.getGroupIDFromBlock(genesisBlock);
            ReadWriteBase gl= blockLedger.getOrCreate(chainId);
            gl.append(genesisBlock);
        } catch (JavaChainException e) {
            e.printStackTrace();
        }
    }
}
