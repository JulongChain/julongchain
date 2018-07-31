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
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.common.privdata.CollectionStoreSupport;
import org.bcia.julongchain.core.common.privdata.IPrivDataSupport;
import org.bcia.julongchain.core.ledger.IStateListener;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;
import org.bcia.julongchain.protos.node.SmartContractDataPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * kv账本监听
 *
 * @author sunzongyu
 * @date 2018/04/16
 * @company Dingxuan
 */
public class KVLedgerLSSCStateListener implements IStateListener {
    private static final JavaChainLog log = JavaChainLogFactory.getLog(KVLedgerLSSCStateListener.class);

    private IPrivDataSupport privdata = new CollectionStoreSupport();

    @Override
    public void handleStateUpdates(String ledgerID, List<KvRwset.KVWrite> stateUpdates) throws JavaChainException {
        log.debug("Group [{}]: Handling state updates in LSSC namespace - stateUpdate", ledgerID);
        List<SmartContractDefinition> scDefinitions = new ArrayList<>();
        for(KvRwset.KVWrite kvWrite : stateUpdates){
            // There are LSCC entries for the chaincode and for the chaincode collections.
            // We need to ignore changes to chaincode collections, and handle changes to chaincode
            // We can detect collections based on the presence of a CollectionSeparator, which never exists in chaincode names
            if (privdata.isCollectionConfigKey(kvWrite.getKey())) {
                continue;
            }
            // Ignore delete event
            if (kvWrite.getIsDelete()) {
                continue;
            }
            log.info("Group [{}]: Handling LSSC state update for smartcontract {}", ledgerID, kvWrite.getKey());
            SmartContractDataPackage.SmartContractData smartContractData = null;
            try {
                smartContractData = SmartContractDataPackage.SmartContractData.parseFrom(kvWrite.getValue());
            } catch (InvalidProtocolBufferException e) {
                log.error(e.getMessage(), e);
                throw new JavaChainException(e);
            }
            scDefinitions.add(new SmartContractDefinition(smartContractData.getName(), smartContractData.getVersion(), smartContractData.getId().toByteArray()));
        }
        ScEventManager.getMgr().handleSmartContractDeploy(ledgerID, (SmartContractDefinition[]) scDefinitions.toArray());
    }
}
