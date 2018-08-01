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
import org.bcia.julongchain.common.util.BytesHexStrTranslate;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.IVersionedDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.UpdateBatch;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedValue;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;
import org.bcia.julongchain.core.ledger.sceventmgmt.ISmartContractLifecycleEventListener;
import org.bcia.julongchain.core.ledger.sceventmgmt.SmartContractDefinition;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.csp.gmt0016.excelsecu.bean.Version;
import org.bcia.julongchain.node.util.LedgerUtils;
import org.lightcouch.CouchDbClient;
import org.omg.CORBA.ByteHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final int MAX_DBNAME_LENGTH = 238;
    private static final int GROUP_NAME_ALLOWED_LENGTH = 50;
	private static final int NAMESPACE_NAME_ALLOWED_LENGTH = 50;
	private static final int COLLECTION_NAME_ALLOWED_LENGTH = 50;
    private static final String EXPECTED_DBNAME_PATTERN = "[a-z][a-z0-9.$_()-]*";

    static{
        dbArtifactsDirFilter.put("META-INF/statedb/couchdb/indexes", true);
    }

    private String groupName;
	private CouchDbClient dbInstance;
	/**
	 * Every group has their own metadataDB to store metadata such as savepoint
	 */
    private CouchDbClient metadataDB;
    private Map<String, CouchDbClient> namespaceDBs;


    public VersionedCouchDB(CouchDbClient dbInstance, String dbName) {
		this.groupName = dbName;
		this.namespaceDBs = new HashMap<>();
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
			dbName += "(" + BytesHexStrTranslate.bytesToHexFun1(Util.getHashBytes(untruncatedDBName.getBytes())) + ")";
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
			String hashOfNamespaceDBName = BytesHexStrTranslate.bytesToHexFun1(Util.getHashBytes((groupName + "_" + nsName).getBytes()));
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
			// TODO: 7/19/18 create databases
		}
		namespaceDBs.put(namespace, db);
		return db;
	}

	public static void main(String[] args) throws Exception {
		VersionedCouchDB versionedCouchDB = new VersionedCouchDB(null, null);
		Pattern pattern = Pattern.compile(EXPECTED_DBNAME_PATTERN);
		String input = versionedCouchDB.constructNamespaceName("myGtoup", "mycc");
		Matcher matcher = pattern.matcher(input);
		boolean matches = matcher.matches();
		System.out.println(input);
		System.out.println(matches);
	}

    @Override
    public VersionedValue getState(String namespace, String key) throws LedgerException {
        return null;
    }

    @Override
    public LedgerHeight getVersion(String namespace, String key) throws LedgerException {
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
    public void applyUpdates(UpdateBatch batch, LedgerHeight height) throws LedgerException {

    }

    @Override
    public LedgerHeight getLatestSavePoint() throws LedgerException {
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
		// TODO: 7/19/18 couchdb create index
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
