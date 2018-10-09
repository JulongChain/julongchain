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

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.ledger.couchdb.CouchDB;
import org.bcia.julongchain.core.ledger.couchdb.CouchDBDefinition;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.IVersionedDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.IVersionedDBProvider;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 提供leveldb实现的VersionDB辅助
 *
 * @author sunzongyu
 * @date 2018/05/22
 * @company Dingxuan
 */
public class VersionedCouchDBProvider implements IVersionedDBProvider {
    private static JulongChainLog log = JulongChainLogFactory.getLog(VersionedCouchDBProvider.class);

	private CouchDbClient dbInstance;
	private Map<String, IVersionedDB> databases;


    public VersionedCouchDBProvider(){
        log.debug("Constructing CouchDB VersionedDBProvider");
		CouchDBDefinition couchDBDefinition = new CouchDBDefinition();
		CouchDbProperties properties = new CouchDbProperties(
				couchDBDefinition.getUserName(),
				true,
				"http",
				couchDBDefinition.getHost(),
				couchDBDefinition.getPort(),
				couchDBDefinition.getUserName(),
				couchDBDefinition.getPassword()

		)
				.setConnectionTimeout(couchDBDefinition.getRequestTimeOut())
				.setMaxConnections(couchDBDefinition.getMaxRetriesOnStartUp());
		this.databases = new HashMap<>();
		this.dbInstance = new CouchDbClient(properties);
	}

    @Override
    public synchronized IVersionedDB getDBHandle(String dbName) throws LedgerException {
		if(databases.containsKey(dbName)){
			return databases.get(dbName);
		} else {
			VersionedCouchDB vdb = new VersionedCouchDB(this.dbInstance, dbName);
			databases.put(dbName, vdb);
			return vdb;
		}
    }

    @Override
    public void close() {
    	//nothing to do
    }
}
