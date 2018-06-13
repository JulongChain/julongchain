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
package org.bcia.julongchain.core.ledger.sceventmgmt;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.common.smartcontractprovider.SmartContractProvider;
import org.bcia.julongchain.core.common.sysscprovider.SystemSmartContractProvider;
import org.bcia.julongchain.core.ledger.IQueryExecutor;
import org.bcia.julongchain.protos.node.SmartContractDataPackage;

/**
 * 智能合约信息提供者
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class SmartContractInfoProviderImpl implements ISmartContractInfoProvider{
    private static final JavaChainLog log = JavaChainLogFactory.getLog(SmartContractInfoProviderImpl.class);

    @Override
    public boolean isSmartContractDeployed(String groupID, SmartContractDefinition smartContractDefinition) throws JavaChainException{
        return isSmartContractDeployed(groupID, smartContractDefinition.getName(), smartContractDefinition.getVersion(), smartContractDefinition.getHash());
    }

    private boolean isSmartContractDeployed(String groupID, String scName, String scVersion, byte[] scHash) throws JavaChainException{
        SystemSmartContractProvider sscProvider = new SystemSmartContractProvider();
        IQueryExecutor qe = null;
        try {
            qe = sscProvider.getQueryExecutorForLedger(groupID);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        try {
            byte[] scDataBytes = qe.getState("lssc", scName);
            if (scDataBytes == null) {
                log.info("Got null scData");
                return false;
            }
            SmartContractDataPackage.SmartContractData scData = SmartContractDataPackage.SmartContractData.parseFrom(scDataBytes);
            return scData.getVersion().equals(scVersion) && scData.getId().equals(scHash);
        } catch (InvalidProtocolBufferException e){
            log.error("Got wrong scData");
            log.error(e.getMessage(), e);
            throw new JavaChainException(e);
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            qe.done();
        }
    }

    @Override
    public byte[] retrieveSmartContractArtifacts(SmartContractDefinition smartContractDefinition) throws JavaChainException{
        return SmartContractProvider.extractStatedbArtifactsForSmartContract(smartContractDefinition.getName(), smartContractDefinition.getVersion());
    }
}
