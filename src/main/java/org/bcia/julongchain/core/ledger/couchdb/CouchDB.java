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

import com.alibaba.fastjson.JSONObject;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.lightcouch.*;

import java.io.InputStream;
import java.util.List;

/**
 * 提供操作couchdb的操作方法，如增，删，改，查
 *
 * @author zhaiyihua
 * @date 2018/04/20
 * @company ShanghaiGeer
 */
public class CouchDB {

    private static JavaChainLog log = JavaChainLogFactory.getLog(CouchDB.class);

    /**
     * creat a new client connection
     * @param url
     * @param username
     * @param password
     * @param maxConnections
     * @param requestTimeout
     * @return
     * @throws Exception
     */
    public CouchDbClient creatConnectionDB(String url, int port, String username, String password, int maxConnections,
                                  int requestTimeout, String dbName) throws LedgerException{
        CouchDbProperties properties = new CouchDbProperties();
        properties.setPath(url);
        properties.setPort(port);
        properties.setConnectionTimeout(requestTimeout);
        properties.setMaxConnections(maxConnections);
        properties.setDbName(dbName);
        properties.setUsername(username);
        properties.setPassword(password);
        CouchDbClient dbClient = new CouchDbClient(properties);
        return dbClient;
    }

    /**
     * creat a new db
     * @param dbInstance
     * @param dbName
     */
    public void createDatabaseIfNotExist(CouchDbClient dbInstance, String dbName){
        CouchDbContext context = dbInstance.context();
        context.createDB(dbName);
    }

    /**
     * GetDatabaseInfo method provides function to retrieve database information
     * @param db
     * @return
     */
    public CouchDbInfo getDatabaseInfo(CouchDbClient db){
        CouchDbContext context = db.context();
        CouchDbInfo dbInfo = context.info();
        return dbInfo;
    }

    /**
     * close db
     * @param db
     * @throws Exception
     */
    public void closeDatabase(CouchDbClient db) throws Exception{
        db.close();
    }

    /**
     * delete IDB
     * @param db
     * @param dbName
     */
    public void dropDatabase(CouchDbClient db, String dbName){
        CouchDbContext context = db.context();
        context.deleteDB(dbName, "delete database");
    }

    /**
     * EnsureFullCommit calls _ensure_full_commit for explicit fsync
     * @param db
     */
    public void ensureFullCommit(CouchDbClient db){
        CouchDbContext context = db.context();
        context.ensureFullCommit();
    }

    /**
     * SaveDoc method provides a function to save a document, id and byte array
     * @param db
     * @param id
     * @param rev
     * @param name
     * @param contentType
     * @param doc
     * @return
     */
    public String saveDoc(CouchDbClient db, String id, String rev, String name,
                          String contentType, InputStream doc){
        Response response = db.saveAttachment(doc, name, contentType, id, rev);
        String error = response.getError();
        return error;
    }

    /**
     * DeleteDoc method provides function to delete a document from the database by id
     * @param db
     * @param id
     * @param rev
     * @return
     */
    public String deleteDoc(CouchDbClient db, String id, String rev){
        Response response = db.remove(id, rev);
        String error = response.getError();
        return error;
    }

    /**
     * getDocumentRevision will return the revision if the document exists, otherwise it will return ""
     * @param id
     * @return
     */
    public String getDocumentRevision(CouchDbClient db, String id) throws Exception{
        JSONObject object  = readDoc(db, id);
        String rev = (String)object.get("_rev");
        return rev;
    }

    /**
     *ReadDoc method provides function to retrieve a document and its revision
     *from the database by id
     * @param db
     * @param id
     * @return
     */
    public JSONObject readDoc(CouchDbClient db, String id){
        try {
        InputStream inputStream = db.find(id);
            JSONObject jsonObject = inputToJson(inputStream);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     *
     * @param db
     * @param startKey
     * @param endKey
     * @param limit
     * @param skip
     * @return
     */
    public JSONObject readDocRange(CouchDbClient db, String startKey, String endKey,
                             int limit, int skip){
        try {
        View view = db.view("_all_docs").endKeyDocId(endKey).startKeyDocId(startKey)
                .limit(limit).skip(skip).includeDocs(true).inclusiveEnd(false);
        InputStream inputStream = view.queryForStream();
            JSONObject jsonObject = inputToJson(inputStream);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * QueryDocuments met fion forunctocessing a query
     * @param db
     * @param queryString
     * @param classOfT
     * @param <T>
     * @return
     */
    public <T>List<T> queryDocuments(CouchDbClient db, String queryString, Class<T> classOfT){
        List<T> docs = db.findDocs(queryString, classOfT);
        return docs;
    }

    /**
     * BatchUpdateDocuments - batch method to batch update documents
     * @param db
     * @param list
     * @param newEdits
     * @return
     */
    public List BatchUpdateDocuments(CouchDbClient db, List<Object> list, boolean newEdits){
        List<Response> responses = db.bulk(list, newEdits);
        return responses;
    }



    /**
     * InputStream to JSONObject
     * @param inputStream
     * @return
     * @throws Exception
     */
    public JSONObject inputToJson(InputStream inputStream) throws Exception{
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = inputStream.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        String s = out.toString();
        JSONObject object = JSONObject.parseObject(s);
        return object;
    }

}
