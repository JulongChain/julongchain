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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.statecouchdb;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.IVersionedDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.UpdateBatch;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedValue;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.julongchain.core.ledger.sceventmgmt.ISmartContractLifecycleEventListener;
import org.bcia.julongchain.core.ledger.sceventmgmt.SmartContractDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LevelDB实现的VersionDB
 *
 * @author sunzongyu
 * @date 2018/05/22
 * @company Dingxuan
 */
public class VersionedCouchDB implements IVersionedDB, ISmartContractLifecycleEventListener {
    private static final JavaChainLog log = JavaChainLogFactory.getLog(VersionedCouchDB.class);

    private static final String BINARY_WRAPPER = "valueBytes";
    private static final String ID_FIELD = "_id";
    private static final String REV_FIELD = "_rev";
    private static final String VERSION_FIELD = "~version";
    private static final String DELETED_FIELD = "_deleted";
    private static final String[] reservedFields = new String[]{
            BINARY_WRAPPER, ID_FIELD, REV_FIELD, VERSION_FIELD, DELETED_FIELD
    };
    private static final Map<String, Boolean> dbArtifactsDirFilter = new HashMap<>();
    private final int QUERY_SKIP = 0;

    static{
        dbArtifactsDirFilter.put("META-INF/statedb/couchdb/indexes", true);
    }

    private String groupName;

    public VersionedCouchDB newVersionedCouchDB(){

        return new VersionedCouchDB();
    }

    @Override
    public VersionedValue getState(String namespace, String key) throws LedgerException {
        return null;
    }

    @Override
    public Height getVersion(String namespace, String key) throws LedgerException {
        return null;
    }

    @Override
    public List<VersionedValue> getStateMultipleKeys(String namespace, List<String> keys) throws LedgerException {
        return null;
    }

    @Override
    public IResultsIterator getStateRangeScanIterator(String namespace, String startKey, String endKey) throws LedgerException {
        return null;
    }

    @Override
    public IResultsIterator executeQuery(String namespace, String query) throws LedgerException {
        return null;
    }

    @Override
    public void applyUpdates(UpdateBatch batch, Height height) throws LedgerException {

    }

    @Override
    public Height getLatestSavePoint() throws LedgerException {
        return null;
    }

    @Override
    public void validateKeyValue(String key, byte[] value) throws LedgerException {

    }

    @Override
    public void open() throws LedgerException {

    }

    @Override
    public void close() throws LedgerException {

    }

    @Override
    public boolean bytesKeySuppoted() {
        return false;
    }

    @Override
    public void handleSmartContractDeploy(SmartContractDefinition smartContractDefinition, byte[] dbArtifactsTar) throws JavaChainException {
        log.debug("Enterint handleSmartContractDeploy()");
        if(smartContractDefinition == null){
            throw new JavaChainException("Found null smartContractDefinition while creating couchdb index on group " + groupName);
        }
    }

    public static String getBinaryWrapper() {
        return BINARY_WRAPPER;
    }

    public static String getIdField() {
        return ID_FIELD;
    }

    public static String getRevField() {
        return REV_FIELD;
    }

    public static String getVersionField() {
        return VERSION_FIELD;
    }

    public static String getDeletedField() {
        return DELETED_FIELD;
    }

    public static String[] getReservedFields() {
        return reservedFields;
    }

    public static Map<String, Boolean> getDbArtifactsDirFilter() {
        return dbArtifactsDirFilter;
    }
}
