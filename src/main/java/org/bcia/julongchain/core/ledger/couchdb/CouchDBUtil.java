/**
 * Copyright ShanghaiGeer. All Rights Reserved.
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
package org.bcia.julongchain.core.ledger.couchdb;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbContext;

/**
 * creat System couchdb
 *
 * @author zhaiyihua
 * @date 2018/04/20
 * @company ShanghaiGeer
 */
public class CouchDBUtil {

    //
    public void createDatabaseIfNotExist(CouchDbClient dbInstance, String dbName){
        CouchDbContext context = dbInstance.context();
        context.createDB(dbName);
    }

    //CreateSystemDatabasesIfNotExist - creates the system databases if they do not exist
    public void CreateSystemDatabasesIfNotExist(CouchDbClient dbInstance){
        CouchDB couchDB = new CouchDB();
        String dbName1 = "_users";
        couchDB.createDatabaseIfNotExist(dbInstance, dbName1);
        String dbName2 = "_replicator";
        couchDB.createDatabaseIfNotExist(dbInstance, dbName2);
        String dbName3 = "_global_changes";
        couchDB.createDatabaseIfNotExist(dbInstance, dbName2);
    }
}
