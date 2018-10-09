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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.ledger.couchdb.CouchDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.IBulkOptimizable;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.IVersionedDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.CompositeKey;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.UpdateBatch;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedValue;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.sceventmgmt.ISmartContractLifecycleEventListener;
import org.bcia.julongchain.core.ledger.sceventmgmt.SmartContractDefinition;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bouncycastle.util.encoders.Hex;
import org.lightcouch.CouchDbClient;
import scala.reflect.internal.Trees;

import java.util.ArrayList;
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
public class VersionedCouchDB implements IVersionedDB, ISmartContractLifecycleEventListener, IBulkOptimizable {
	private static JulongChainLog log = JulongChainLogFactory.getLog(VersionedCouchDB.class);

    private static final String BINARY_WRAPPER = "valueBytes";
    private static final String ID_FIELD = "_id";
    private static final String REV_FIELD = "_rev";
    private static final String VERSION_FIELD = "~version";
    private static final String DELETED_FIELD = "_deleted";
    private final int QUERY_SKIP = 0;
    private static final int MAX_DBNAME_LENGTH = 238;
    private static final int GROUP_NAME_ALLOWED_LENGTH = 50;
	private static final int NAMESPACE_NAME_ALLOWED_LENGTH = 50;
	private static final int COLLECTION_NAME_ALLOWED_LENGTH = 50;
    private static final String EXPECTED_DBNAME_PATTERN = "[a-z][a-z0-9.$_()-]*";
	private static final String RETURN_VALUE_INDEX = "returnValueIndex";
	private static final String RETURN_VERSION_INDEX = "returnVersionIndex";

	private static Map<String, Boolean> dbArtifactsDirFilter = new HashMap<>();
	private static String[] reservedFields = new String[]{
			BINARY_WRAPPER, ID_FIELD, REV_FIELD, VERSION_FIELD, DELETED_FIELD
	};
    static{
        dbArtifactsDirFilter.put("META-INF/statedb/couchdb/indexes", true);
    }

    private String groupName;
    private CouchDB couchDB;
	private CouchDbClient dbInstance;
	/**
	 * Every group has their own metadataDB to store metadata such as savepoint
	 */
    private CouchDbClient metadataDB;
    private Map<String, CouchDbClient> namespaceDBs;
    private CommittedVersions committedDataCache;


    public VersionedCouchDB(CouchDbClient dbInstance, String dbName) throws LedgerException {
		this.couchDB = new CouchDB();
		this.groupName = dbName;
		dbName = constructMetadataName(dbName);
		couchDB.createDatabaseIfNotExist(dbInstance, dbName);
		this.metadataDB = dbInstance;
		this.dbInstance = dbInstance;
		this.namespaceDBs = new HashMap<>(32);
		this.committedDataCache = new CommittedVersions();
	}

	/**
	 * final dbName:dbName	50chars
	 * 				"("		1char
	 *				hash	32chars
	 *				")"		1char
	 *				"_"		1char
	 *				total	85chars
	 */
	private String constructMetadataName(String dbName) throws LedgerException {
		String untruncatedDBName = dbName;
		if (dbName.length() > MAX_DBNAME_LENGTH) {
			dbName = dbName.substring(0, GROUP_NAME_ALLOWED_LENGTH);
			dbName += "(" + Hex.toHexString(Util.getHashBytes(untruncatedDBName.getBytes())) + ")";
		}
		dbName += "_";
		log.debug("Modify " + untruncatedDBName + " to " + dbName);
		return dbName;
	}

	/**
	 * For namespaceDBName form groupName_namespace$$collection
	 * The final namespaceDBName is	50 chars of groupName
	 * 								"_"
	 * 								50 chars of namespace
	 * 								"$$"
	 * 								50chars of collection
	 * 								hash of groupName_namespace$$collection
	 *
	 * For namespaceDBName form groupName_namespace
	 * The final namespaceDBName is	50 chars of groupName
	 * 								"_"
	 * 								50 chars of namespace
	 * 								hash of groupName_namespace
	 */
	private String constructNamespaceName(String groupName, String nsName) throws LedgerException {
		String escapeNamespace = escapeUpperCase(nsName);
		String namespaceDBName = groupName + "_" + escapeNamespace;
		if (namespaceDBName.length() > MAX_DBNAME_LENGTH) {
			String hashOfNamespaceDBName = Hex.toHexString(Util.getHashBytes((groupName + "_" + nsName).getBytes()));
			if (groupName.length() > GROUP_NAME_ALLOWED_LENGTH) {
				groupName = groupName.substring(0, GROUP_NAME_ALLOWED_LENGTH);
			}
			String[] names = escapeNamespace.split("\\$\\$");
			String namespace = names[0];
			if (namespace.length() > NAMESPACE_NAME_ALLOWED_LENGTH) {
				namespace = namespace.substring(0, NAMESPACE_NAME_ALLOWED_LENGTH);
			}
			escapeNamespace = namespace;

			if (names.length == 2) {
				String collection = names[1];
				if (collection.length() > COLLECTION_NAME_ALLOWED_LENGTH) {
					collection = collection.substring(0, COLLECTION_NAME_ALLOWED_LENGTH);
				}
				escapeNamespace = escapeNamespace + "$$" + collection;
			}
			return groupName + "_" + escapeNamespace + "(" + hashOfNamespaceDBName + ")";
		}
		return namespaceDBName;
	}

	/**
	 * Add "$" before all UPPERCASE chars and modify all chars to LOWERCASE
	 * e.g "a123AdsdasASKLDJH" -> "a123$adsdas$a$s$k$l$d$j$h"
	 */
	private String escapeUpperCase(String dbName) {
		return dbName.replaceAll("[A-Z]", "\\$$0").toLowerCase();
	}

	// TODO: 7/18/18 unfinished
	private String mapAndValidateDBName(String dbName) throws LedgerException {
		if (dbName.length() <= 0) {
			String errMsg = "DB name is not supposed to be empty";
			log.error(errMsg);
			throw new LedgerException(errMsg);
		}
		if (dbName.length() > MAX_DBNAME_LENGTH) {
			String errMsg = "DB name is not supposed to be longer than " + MAX_DBNAME_LENGTH;
			log.error(errMsg);
			throw new LedgerException(errMsg);
		}
		return null;
	}

	private synchronized CouchDbClient getNamespaceDBHandle(String namespace) throws LedgerException {
		if (namespaceDBs.containsKey(namespace)) {
			return namespaceDBs.get(namespace);
		}
		namespace = constructNamespaceName(groupName, namespace);
		CouchDbClient db = namespaceDBs.get(namespace);
		if (db == null) {
			couchDB.createDatabaseIfNotExist(dbInstance, namespace);
		}
		namespaceDBs.put(namespace, db);
		return db;
	}

    @Override
    public VersionedValue getState(String namespace, String key) throws LedgerException {
		log.debug("GetState : ns = " + namespace + " key = " + key);
		CouchDbClient db = getNamespaceDBHandle(namespace);
		JSONObject doc = couchDB.readDoc(db, key);
		if (doc == null) {
			return null;
		}

		return null;
    }

    @Override
    public LedgerHeight getHeight(String namespace, String key) throws LedgerException {
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

	/**
	 * 执行修改世界状态
	 */
    @Override
    public synchronized void applyUpdates(UpdateBatch batch, LedgerHeight height) throws LedgerException {
    	//更新包中包含的namespace(scID)
		List<String> namespaces = batch.getUpdatedNamespaces();
		//将要执行的更新包
		UpdateBatch processBatch = new UpdateBatch();
		//不存在的key
		List<CompositeKey> missingKeys = new ArrayList<>();
		//错误
		List<Exception> errResponse = new ArrayList<>();
		for (String namespace : namespaces) {
			//最大的更新包大小
			int maxBatchSize = LedgerConfig.getMaxBatchSize();
			//更新包计数器
			int batchSizeCounter = 0;
			//对每一个namespace的更新包
			Map<String, VersionedValue> nsUpdates = batch.getUpdates(namespace);
			for (Map.Entry<String, VersionedValue> entry : nsUpdates.entrySet()) {
				String k = entry.getKey();
				VersionedValue vv = entry.getValue();
				//计数器+1
				batchSizeCounter++;
				CompositeKey compositeKey = new CompositeKey(namespace, k);
				//判断是否存在key
				if (committedDataCache.getRevisionNumbers().containsKey(compositeKey)) {
					missingKeys.add(compositeKey);
				}
				//是否为删除操作
				if (vv.getValue() == null) {
					processBatch.delete(namespace, k, vv.getHeight());
				} else {
					processBatch.put(namespace, k, vv.getValue(), vv.getHeight());
				}
				//当超过最大更新包大小时，执行一次更新
				if (batchSizeCounter >= maxBatchSize) {
					try {
						processUpdateBatch(processBatch, missingKeys);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						errResponse.add(e);
						continue;
					}
				}
			}
		}
		//执行剩余的更新
		try {
			processUpdateBatch(processBatch, missingKeys);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			errResponse.add(e);
		}
		//存在错误时，执行错误处理
		if (errResponse.size() > 0) {
			StringBuilder errMsg = new StringBuilder("");
			errMsg.append("Got ");
			errMsg.append(errResponse.size());
			errMsg.append(" errors, when apply updates");
			log.error(errMsg.toString());
			throw new LedgerException(errMsg.toString());
		}

		recordSavepoint(height, namespaces);
	}

    @Override
    public LedgerHeight getLatestSavePoint() throws LedgerException {
        return null;
    }

    @Override
    public void validateKeyValue(String key, Object value) throws LedgerException {
		checkReservedFieldsNotUsed((JSONObject) value);
	}

    @Override
    public void open() throws LedgerException {
		//nothing to do
    }

    @Override
    public void close() throws LedgerException {
		//nothing to do
    }

    @Override
    public boolean bytesKeySuppoted() {
        return false;
    }

    @Override
    public void handleSmartContractDeploy(SmartContractDefinition smartContractDefinition, byte[] dbArtifactsTar) throws JulongChainException {
        log.debug("Entering handleSmartContractDeploy()");
        if(smartContractDefinition == null){
            throw new JulongChainException("Found null smartContractDefinition while creating couchdb index on group " + groupName);
        }
		// TODO: 7/19/18 couchdb create index
	}

	private void checkReservedFieldsNotUsed(Map<String, Object> jsonMap) throws LedgerException {
		for (String fieldName : reservedFields) {
			if (jsonMap.containsKey(fieldName)) {
				throw new LedgerException("The reserved field [" + fieldName + "] was found");
			}
		}
	}

	private void processUpdateBatch(UpdateBatch updateBatch, List<CompositeKey> missingKeys) throws Exception {
		if (missingKeys.size() > 0) {
			log.debug("Retrieving keys with unknown revision numbers\nkeys=" + JSON.toJSONString(missingKeys));
			loadCommittedVersions(missingKeys);
		}

//		Map<String, >
	}

	private void recordSavepoint(LedgerHeight height, List<String> namespaces) throws LedgerException {

	}

	private Map<String, byte[]> getValueAndVersionFromDoc(byte[] persistedValue) {
		return null;
	}

	@Override
	public void loadCommittedVersions(List<CompositeKey> keys) throws LedgerException{

	}

	@Override
	public LedgerHeight getCachedVersion(String ns, String key) {
		return null;
	}

	@Override
	public void clearCachedVersions() {

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
