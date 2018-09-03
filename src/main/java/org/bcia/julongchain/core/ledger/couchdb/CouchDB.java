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
import com.google.common.collect.Lists;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.lightcouch.*;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 提供操作couchdb的操作方法，如增，删，改，查
 *
 * @author zhaiyihua
 * @date 2018/04/20
 * @company ShanghaiGeer
 */
public class CouchDB {

    private static JulongChainLog log = JulongChainLogFactory.getLog(CouchDB.class);
    public int indexWarmCounter;
    private CouchDbClientBase dbc;

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
                                  int requestTimeout, String dbName,String protocol) throws LedgerException{
        CouchDbProperties properties = new CouchDbProperties();
        properties.setHost(url);
        properties.setPort(port);
        properties.setConnectionTimeout(requestTimeout);
        properties.setMaxConnections(maxConnections);
        properties.setDbName(dbName);
        properties.setUsername(username);
        properties.setPassword(password);
        properties.setProtocol(protocol);
        CouchDbClient dbClient = new CouchDbClient(properties);
        indexWarmCounter = 0;
        CouchDBUtil.CreateSystemDatabasesIfNotExist(dbClient);
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
        int nBlocks = LedgerConfig.getWarmIndexesAfterNBlocks();
        if(LedgerConfig.isAutoWarmIndexesEnabled()){
            if (indexWarmCounter >= nBlocks){
                runWarmIndexAllIndexes(db);
                indexWarmCounter = 0;
            }
            indexWarmCounter ++;
        }
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
     *  BatchRetrieveDocumentMetadata - batch method to retrieve document metadata for  a set of keys,
     *  including ID, couchdb revision number, and ledger version
     * @param db
     * @param keys
     * @return
     */
    public List BatchRetrieveDocumentMetadata(CouchDbClient db, List<String> keys){
        List<Object> docs = db.view("_all_docs")
                .includeDocs(true)
                .keys(keys)
                .query(Object.class);
        return docs;
    }

    /**
     * WarmIndex method provides a function for warming a single index
     * @param designdoc
     * @param indexname
     * @return
     */
    public Boolean warmIndex(CouchDbClient db, String designdoc,String indexname){
        Boolean boolFalg = false;
        try {
            List<NameValuePair> params = Lists.newArrayList();
            params.add(new BasicNameValuePair("stale", "update_after"));
            String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));

            String str = String.format("_design/%s/_view/%s?" + paramStr, designdoc, indexname);
            URI build = URIBuilderUtil.buildUri(db.getDBUri()).path(str).build();
            HttpGet get = new HttpGet(build);
            HttpResponse response = db.executeRequest(get);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200){
                boolFalg = true;
            }
        } catch (Exception e) {
            boolFalg = false;
            e.printStackTrace();
        }
        return boolFalg;
    }

    /**
     * ListIndex method lists the defined indexes for a database
     * @return
     */
    public List listIndex(CouchDbClient db){
        try {
            List arraylist = new ArrayList();
            URI uri = db.getDBUri();
            URI build = URIBuilderUtil.buildUri(uri).path("_index/").build();
            HttpGet get = new HttpGet(build);
            get.addHeader("Accept", "application/json");
            HttpResponse response = db.executeRequest(get);
            InputStream stream = CouchDBUtil.getStream(response);
            JSONObject jsonObject = inputToJson(stream);
            List indexes = (List) jsonObject.get("indexes");
            for(int i = 0 ; i < indexes.size() ; i++) {
                Map hashmap = new HashMap();
                Map map = (Map) indexes.get(i);
                if((String) map.get("ddoc") == null){
                    continue;
                }else {
                    hashmap.put("Name",map.get("name"));
                    String ddoc = (String) map.get("ddoc");
                    hashmap.put("DesignDoc",ddoc.split("/")[1]);
                    arraylist.add(hashmap);
                }
            }
            log.info(jsonObject.toString());
            return arraylist;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * CreateIndex method provides a function creating an index
     * @return
     */
    public Boolean creatIndex(CouchDbClient db, String indexdefinition){
        Boolean boolFalg = false;
        try {
            Boolean bool = isJson(indexdefinition);
            if (!bool){
                log.info("JSON format is not valid");
                return false;
            }
            URI uri = db.getDBUri();
            URI build = URIBuilderUtil.buildUri(uri).path("_index/").build();
            HttpPost post = new HttpPost(build);
            setEntity(post, indexdefinition);
            HttpResponse response = db.executeRequest(post);
            InputStream stream = CouchDBUtil.getStream(response);
            JSONObject jsonObject = inputToJson(stream);
            String result = (String) jsonObject.get("Result");
            if("created".equals(result)){
                boolFalg = true;
            }
        } catch (Exception e) {
            boolFalg = false;
            e.printStackTrace();
        }
        return boolFalg;
    }

    /**
     * WarmIndexAllIndexes method provides a function for warming all indexes for a database
     * @return
     */
    public Boolean warmIndexAllIndexes(CouchDbClient db){
        Boolean index = false;
        List<Map<String, String>> list = listIndex(db);
        for (Map<String, String> map : list) {
            index = warmIndex(db, map.get("DesignDoc"), map.get("Name"));
        }
        return index;
    }

    /**
     * runWarmIndexes is a wrapper for WarmIndexAllIndexes to catch andrt any errors
     * @param db
     * @return
     */
    public Boolean runWarmIndexAllIndexes(CouchDbClient db){
        Boolean indexes = warmIndexAllIndexes(db);
        return indexes;
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

    public void setEntity(HttpEntityEnclosingRequestBase httpRequest, String json) {
        StringEntity entity = new StringEntity(json, "UTF-8");
        entity.setContentType("application/json");
        httpRequest.setEntity(entity);
    }


    public Boolean isJson(String str){
        try {
            JSONObject jsonStr= JSONObject.parseObject(str);
            return  true;
        } catch (Exception e) {
            return false;
        }
    }

}
