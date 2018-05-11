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
import org.bcia.javachain.protos.node.ProposalPackage;

import java.util.Arrays;
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

    public void initialize(){
        this.infoProvider = new SmartContractInfoProviderImpl();
    }

    /**
     * 将给定的SmartContractLifecycleEventListener注册到给定ledger
     */
    public synchronized void register(String ledgerID, ISmartContractLifecycleEventListener l){
        scLifecycleListeners.put(ledgerID, l);
    }

    /**
     * 智能合约部署
     * TODO 实现依赖couchdb
     * @param ledgerID
     * @param smartContractDefinitions
     */
    public synchronized void handleSmartContractDeploy(String ledgerID, SmartContractDefinition[] smartContractDefinitions){
        logger.debug("handleSmartContractDeploy() - ledgerID = " + ledgerID);
        //设置最后部署的智能合约
        latesSmartContractDeploys.put(ledgerID, smartContractDefinitions);
        for(SmartContractDefinition smartContractDefinition : smartContractDefinitions){
            logger.debug(String.format("Group [%s]: Handling smartContract deploy event for smartContract [%s]", ledgerID, smartContractDefinition));
            //获取db实例,
            byte[] dbArtifacts = infoProvider.retrieveSmartContractArtifacts(smartContractDefinition);
            //!installed, 无需完成智能合约实例
            if(dbArtifacts != null){
                logger.info(String.format("Group [%s]: SmartContract [%s] is not installed so that no need to create SmartContract artifact", ledgerID, smartContractDefinition));
                continue;
            }
            //执行部署
            invokeHandler(ledgerID, smartContractDefinition, dbArtifacts);
            logger.debug(String.format("Group [%s]: Handled smartcontract deploy event for smartcontract [%s]", ledgerID, smartContractDefinition));
        }
    }

    /**
     * 智能合约安装
     * @param smartContractDefinition
     * @param dbArtifacts
     */
    public synchronized void handleSmartContractInstall(SmartContractDefinition smartContractDefinition, byte[] dbArtifacts){
        logger.debug("handleSmartContractInstall() - smartContractDefinition= " + smartContractDefinition);
        for(Map.Entry<String, ISmartContractLifecycleEventListener> entry : scLifecycleListeners.entrySet()){
            String ledgerID = entry.getKey();
            logger.debug(String.format("Group [%s]: Handling smartcontract install event for smartcontract [%s]", ledgerID, smartContractDefinition));
            boolean deployed = isSmartContractPresentInLatestDeploys(ledgerID, smartContractDefinition);
            if(!deployed){
                deployed = infoProvider.isSmartContractDeployed(ledgerID, smartContractDefinition);
            }
            if(!deployed){
                logger.info(String.format("Group [%s]: SmartContract [%s] is not installed so that no need to create SmartContract artifact", ledgerID, smartContractDefinition));
                continue;
            }
            invokeHandler(ledgerID, smartContractDefinition, dbArtifacts);
            logger.debug(String.format("Group [%s]: Handled smartcontract deploy event for smartcontract [%s]", ledgerID, smartContractDefinition));
        }
    }

    /**
     * 执行智能合约部署
     * @param ledgerID
     * @param smartContractDefinition
     * @param dbArtifactsTar
     */
    private void invokeHandler(String ledgerID, SmartContractDefinition smartContractDefinition, byte[] dbArtifactsTar){
        ISmartContractLifecycleEventListener listener = scLifecycleListeners.get(ledgerID);
        if(listener == null){
            return;
        }
        //TODO fabric中实现依赖couchdb
        listener.handleSmartContractDeploy(smartContractDefinition, dbArtifactsTar);
    }

    /**
     * 判断当前智能合约是否已经部署
     * @param ledgerID
     * @param smartContractDefinition
     * @return
     */
    private boolean isSmartContractPresentInLatestDeploys(String ledgerID, SmartContractDefinition smartContractDefinition){
        SmartContractDefinition[] scDefs = latesSmartContractDeploys.get(ledgerID);
        if(scDefs == null){
            return false;
        }
        for(SmartContractDefinition scDef : scDefs){
            if(scDef.getName().equals(smartContractDefinition.getName()) && scDef.getVersion().equals(smartContractDefinition.getVersion()) && Arrays.equals(scDef.getHash(), smartContractDefinition.getHash())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void handleStateUpdates(String ledgerID, List<KvRwset.KVWrite> stateUpdates) {

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
}
