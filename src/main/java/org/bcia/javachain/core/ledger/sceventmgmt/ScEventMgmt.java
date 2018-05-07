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
package org.bcia.javachain.core.ledger.sceventmgmt;

import jdk.nashorn.internal.runtime.regexp.joni.constants.EncloseType;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.StateListener;
import org.bcia.javachain.core.ledger.StateUpdates;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class ScEventMgmt implements StateListener {
    private static final JavaChainLog logger  = JavaChainLogFactory.getLog(ScEventMgmt.class);
    private ISmartContractInfoProvider infoProvider = null;
    private Map<String, ISmartContractLifecycleEventListener> scLifecycleListeners = new HashMap<>();
    private Map<String, SmartContractDefinition[]> latesSmartContractDeploys = new HashMap<>();
    private static ScEventMgmt mgmt = null;

    /**
     * 单例获取Mgr实例
     */
    public static ScEventMgmt getMgr(){
        if(mgmt == null){
            mgmt = new ScEventMgmt();
            mgmt.setInfoProvider(new SmartContractInfoProviderImpl());
        }
        return mgmt;
    }

    public ISmartContractInfoProvider getInfoProvider() {
        return infoProvider;
    }

    public void setInfoProvider(ISmartContractInfoProvider infoProvider) {
        this.infoProvider = infoProvider;
    }

    public Map<String, ISmartContractLifecycleEventListener> getScLifecycleListeners() {
        return scLifecycleListeners;
    }

    public void setScLifecycleListeners(Map<String, ISmartContractLifecycleEventListener> scLifecycleListeners) {
        this.scLifecycleListeners = scLifecycleListeners;
    }

    public Map<String, SmartContractDefinition[]> getLatesSmartContractDeploys() {
        return latesSmartContractDeploys;
    }

    public void setLatesSmartContractDeploys(Map<String, SmartContractDefinition[]> latesSmartContractDeploys) {
        this.latesSmartContractDeploys = latesSmartContractDeploys;
    }

    public void initialize(){
        this.infoProvider = new SmartContractInfoProviderImpl();
    }

    /**
     * 将给定的SmartContractLifecycleEventListener注册到给定ledger
     */
    public synchronized void register(String ledgerID, ISmartContractLifecycleEventListener l){
        scLifecycleListeners.put(ledgerID, l);
    }

    public synchronized void handleSmartContractDeploy(String legetID, SmartContractDefinition[] smartContractDefinitions){
        latesSmartContractDeploys.put(legetID, smartContractDefinitions);
        for(SmartContractDefinition smartContractDefinition : smartContractDefinitions){
            byte[] dbArtifacts = infoProvider.retrieveSmartContractArtifacts(smartContractDefinition);
            //TODO !installed

            invokeHandler(legetID, smartContractDefinition, dbArtifacts);
           logger.debug(String.format("Gtoup [%s]: Handle smartcontract deploy event for smartcontract [%s]", legetID, smartContractDefinition));
        }
    }

    public synchronized void handleSmartContractInstall(SmartContractDefinition smartContractDefinition, byte[] dbArtifacts){
        logger.debug("handleSmartContractInstall() - smartContractDefinition= " + smartContractDefinition);
        for(Map.Entry<String, ISmartContractLifecycleEventListener> entry : scLifecycleListeners.entrySet()){
            String ledgerID = entry.getKey();
            logger.debug(String.format("Gtoup [%s]: Handling smartcontract install event for smartcontract [%s]", ledgerID, smartContractDefinition));

        }
    }

    private void invokeHandler(String ledgerID, SmartContractDefinition smartContractDefinition, byte[] dbArtifactsTar){
        ISmartContractLifecycleEventListener listener = scLifecycleListeners.get(ledgerID);
        if(listener == null){
            return;
        }
        listener.handleSmartContractDeploy(smartContractDefinition, dbArtifactsTar);
    }

    @Override
    public void handleStateUpdates(String ledgerID, List<KvRwset.KVWrite> stateUpdates) {

    }
}
