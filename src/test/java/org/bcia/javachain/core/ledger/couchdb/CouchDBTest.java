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
package org.bcia.javachain.core.ledger.couchdb;

import com.alibaba.fastjson.JSONObject;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.node.NodeConfig;
import org.bcia.javachain.core.node.NodeConfigFactory;
import org.junit.Test;
import org.lightcouch.*;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * test couchdb
 *
 * @author zhaiyihua
 * @date 2018/05/10
 * @company ShanghaiGeer
 */
public class CouchDBTest {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CouchDBTest.class);

    public CouchDbClient creatConnectionDB() throws LedgerException {
        CouchDbProperties properties = new CouchDbProperties();
        String url = "192.168.122.128";
        int port = 9000;
        int requestTimeout = 1000;
        int maxConnections = 5;
        String username = "admin";
        String password = "123456";
        properties.setDbName("javachain");
        properties.setHost(url);
        properties.setProtocol("http");
        properties.setPort(port);
        properties.setUsername(username);
        properties.setPassword(password);
        CouchDbClient dbClient = new CouchDbClient(properties);
        return dbClient;
    }

    @Test
    public void CreateDatabaseIfNotExist() throws LedgerException{
        CouchDB couchDB = new CouchDB();
        CouchDbClient db = creatConnectionDB();
        String dbName = "javachain";
        couchDB.createDatabaseIfNotExist(db, dbName);
    }

    @Test
    public void getDatabaseInfo() throws LedgerException{
        CouchDB couchDB = new CouchDB();
        CouchDbClient db = creatConnectionDB();
        CouchDbInfo info = couchDB.getDatabaseInfo(db);
        log.info(info.toString());
    }

    @Test
    public void SaveDoc() throws Exception{
        CouchDB couchDB = new CouchDB();
        CouchDbClient db = creatConnectionDB();
        String id = "3";
        String rev = "1-1111111";
        String name = "test";
        String contentType = "text/plain";

        byte[] bytesToDB = "binary data".getBytes();
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytesToDB);
        //doc.read(txt.getBytes());
        couchDB.saveDoc(db, id, null, name, contentType, bytesIn);
    }

    @Test
    public void addAtt() throws Exception{
        CouchDbClient db = creatConnectionDB();
        Map map = new HashMap();
        map.put("asset_name", "marble02");
        map.put("color", "red");
        map.put("size", 2);
        map.put("owner", "tom");
        byte[] bytesToDB = "test data".getBytes();
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytesToDB);
        db.saveAttachment(bytesIn, "testdata", "text/plain", "1", null);
    }

    @Test
    public void SaveDcreateAttachment() throws Exception{
        //{"asset_name":"marble02","color":"red","size":2,"owner":"tom"}
        Map map = new HashMap();
        map.put("asset_name", "marble02");
        map.put("color", "red");
        map.put("size", 2);
        map.put("owner", "tom");
        CouchDbClient db = creatConnectionDB();
        String data ="mydata";
        String name = "test";
        Attachment attachment = new Attachment();
        attachment.setData(data);
        attachment.setContentType("text/plain");
        Document document = new Document();
        document.setId("4");
        document.setAttachments(map);
        //document.setRevision("");
        document.addAttachment(name, attachment);
        db.save(document);
    }

    @Test
    public void DeleteDoc() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String id = "27b8df429dde46d5afe6a88917d08e80";
        String rev = "1-cbafc0fda20483668d9677c8f2ef65a6";
        String s = couchDB.deleteDoc(db, id, rev);
        log.info("test data",s);
    }

    @Test
    public void DropDatabase() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String name = "";
        couchDB.dropDatabase(db, name);
    }

    @Test
    public void readDoc() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String id = "2";
        JSONObject object = couchDB.readDoc(db, id);
        String rev = (String)object.get("_rev");
        // jsonElement.
        log.info(rev);
    }

    @Test
    public void getDocumentRevision() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String id = "3";
        String revision = couchDB.getDocumentRevision(db, id);
        log.info(revision);
    }

    @Test
    public void readDocRange() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String startId = "1";
        String endId = "3";
        int limit = 2;
        int skip = 0;
        JSONObject object = couchDB.readDocRange(db, startId, endId, limit, skip);
        log.info(object.toString());
    }

    @Test
    public void queryDocuments() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String pm = "{\"selector\":{\"owner\":{\"$eq\":\"tom\"}},\"limit\":2}";
        List<Object> docs = couchDB.queryDocuments(db, pm, Object.class);
        log.info(docs.toString());
    }

    @Test
    public void BatchUpdateDocuments() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        List<Object> list = new ArrayList();
        Map map1 = new HashMap();
        map1.put("asset_name", "marble04");
        map1.put("color", "red");
        map1.put("size", 2);
        map1.put("owner", "tom");

        Map map2 = new HashMap();
        map2.put("asset_name", "marble05");
        map2.put("color", "red");
        map2.put("size", 2);
        map2.put("owner", "zhangsan");
        list.add(map1);
        list.add(map2);
        List list1 = couchDB.BatchUpdateDocuments(db, list, true);
        log.info(list1.toString());
    }

    @Test
    public void BatchRetrieveDocumentMetadata() throws Exception{
        CouchDbClient db = creatConnectionDB();
        List<Object> list = new ArrayList();
        View docs = db.view("_all_docs");

        list.add("5a52d35c0ea208d776c1d7dff1018a05");
        list.add("5a52d35c0ea208d776c1d7dff1018e8f");
        View view = docs.keys(list);
        log.info(view.toString());
        CouchDB couchDB = new CouchDB();
    }

    @Test
    public void SaveObject() throws Exception{
        CouchDbClient db = creatConnectionDB();
        Map map1 = new HashMap();
        map1.put("asset_name", "marble08");
        map1.put("color", "red");
        map1.put("size", 2);
        map1.put("owner", "tom");
        db.save(map1);
    }


    @Test
    public void cofigTest() throws Exception {
        NodeConfig config = NodeConfigFactory.getNodeConfig();
        NodeConfig.Ledger ledger = config.getLedger();
        NodeConfig.State state = ledger.getState();
        Map<String, String> couchDBConfig = state.getCouchDBConfig();
        String dbAddress = couchDBConfig.get("couchDBAddress");
        log.info(dbAddress);
    }
}
