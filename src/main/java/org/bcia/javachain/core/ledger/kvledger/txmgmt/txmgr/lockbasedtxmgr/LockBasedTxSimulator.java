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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.ledger.TxSimulationResults;

import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public class LockBasedTxSimulator implements ITxSimulator {

    public static LockBasedTxSimulator newLockBasedTxSimulator(LockBasedTxManager txMgr, String txid){
        LockBasedTxSimulator l = new LockBasedTxSimulator();
        return l;
    }

    @Override
    public void setState(String namespace, String key, byte[] value) throws LedgerException {

    }

    @Override
    public void deleteState(String namespace, String key) throws LedgerException {

    }

    @Override
    public void setStateMultipleKeys(String namespace, Map<String, byte[]> kvs) throws LedgerException {

    }

    @Override
    public void executeUPdate(String query) throws LedgerException {

    }

    @Override
    public TxSimulationResults getTxSimulationResults() throws LedgerException {
        return null;
    }

    @Override
    public void setPrivateData(String namespace, String collection, byte[] value) throws LedgerException {

    }

    @Override
    public void setPirvateDataMultipleKeys(String namespace, String collection, Map<String, byte[]> kvs) throws LedgerException {

    }

    @Override
    public void deletePrivateData(String namespace, String collection, String key) throws LedgerException {

    }

    @Override
    public byte[] getState(String namespace, String key) throws LedgerException {
        return new byte[0];
    }

    @Override
    public List<byte[]> getStateMultipleKeys(String namespace, List<String> keys) throws LedgerException {
        return null;
    }

    @Override
    public ResultsIterator getStateRangeScanIterator(String namespace, String startKey, String endKey) throws LedgerException {
        return null;
    }

    @Override
    public ResultsIterator executeQuery(String namespace, String query) throws LedgerException {
        return null;
    }

    @Override
    public byte[] getPrivateData(String namespace, String collection, String key) throws LedgerException {
        return new byte[0];
    }

    @Override
    public List<byte[]> getPrivateDataMultipleKeys(String namespace, String collection, List<String> keys) throws LedgerException {
        return null;
    }

    @Override
    public ResultsIterator getPrivateDataRangeScanIterator(String namespace, String collection, String startKey, String endKey) throws LedgerException {
        return null;
    }

    @Override
    public void done() throws LedgerException {

    }
}
