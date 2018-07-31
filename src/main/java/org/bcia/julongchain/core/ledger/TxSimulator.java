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
package org.bcia.julongchain.core.ledger;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/13
 * @company Dingxuan
 */
public abstract class TxSimulator implements ITxSimulator {

    private static JavaChainLog log = JavaChainLogFactory.getLog(TxSimulator.class);

    @Override
    public void setState(String namespace, String key, byte[] value) throws
            LedgerException {
        log.debug("setState");
        log.debug("namespace:" + namespace + " key:" + key + " " +
                "value:" + value);
    }

    @Override
    public void deleteState(String namespace, String key) throws
            LedgerException {
        log.debug("deleteState");
        log.debug("namespace:" + namespace + " key:" + key);
    }

    @Override
    public void setStateMultipleKeys(String namespace, Map<String, byte[]>
            kvs) throws LedgerException {
        log.debug("setStateMultipleKeys");
        log.debug("namespace:" + namespace + " kvs:" + kvs);
    }

    @Override
    public void executeUpdate(String query) throws LedgerException {
        log.debug("executeUpdate");
        log.debug("query:" + query);
    }

    @Override
    public TxSimulationResults getTxSimulationResults() throws LedgerException {
        log.debug("getTxSimulationResults");
        return new TxSimulationResults();
    }

    @Override
    public byte[] getState(String namespace, String key) throws
            LedgerException {
        log.debug("getState");
        log.debug("namespace:" + namespace + " key:" + key);
        return new byte[0];
    }

    public byte[][] getStateMultipleKeys(String namespace, String[] keys)
            throws LedgerException {
        log.debug("getStateMultipleKeys");
        log.debug("namespace:" + namespace + " keys:" + keys);
        return new byte[0][];
    }

    @Override
    public IResultsIterator getStateRangeScanIterator(String namespace, String
            startKey, String endKey) throws LedgerException {
        log.debug("getStateRangeScanIterator");
        log.debug("namespace:" + namespace + " startKey:" + startKey + " " +
                "endKey:" + endKey);
        return null;
    }

    public IResultsIterator ExecuteQuery(String namespace, String query)
            throws LedgerException {
        log.debug("ExecuteQuery");
        log.debug("namespace:" + namespace + " query:" + query);
        return null;
    }

    @Override
    public void done() {

    }
}
