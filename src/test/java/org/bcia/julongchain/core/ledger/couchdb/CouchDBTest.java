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
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.node.NodeConfig;
import org.bcia.julongchain.core.node.NodeConfigFactory;
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
    private static JulongChainLog log = JulongChainLogFactory.getLog(CouchDBTest.class);

    /**
     * 前置方法，设置了访问couchdb的各个参数
     * @return
     * @throws LedgerException
     */
    public CouchDbClient creatConnectionDB() throws LedgerException {
        CouchDbProperties properties = new CouchDbProperties();
//        String url = "192.168.1.83";
        String url = "127.0.0.1";
        int port = 5984;
        int requestTimeout = 1000;
        int maxConnections = 5;
        String username = "admin";
        String password = "123456";
        properties.setDbName("julongchain");
        properties.setHost(url);
        properties.setProtocol("http");
        properties.setPort(port);
//        properties.setCreateDbIfNotExist(true);
        properties.setUsername(username);
        properties.setPassword(password);
        CouchDbClient dbClient = new CouchDbClient(properties);
        return dbClient;
    }

    /**
     * 测试获取连接对象方法
     * @throws Exception
     */
    @Test
    public void creatConnectionDBTest() throws Exception{
        CouchDB couchDB = new CouchDB();
        CouchDbClient dbClient = couchDB.creatConnectionDB("127.0.0.1", 5984, "admin", "123456", 5, 1000,
                "julongchain", "http");
        log.info(dbClient.toString());
    }

    /**
     * 创建一个库，如果存在则不创建
     * @throws LedgerException
     */
    @Test
    public void CreateDatabaseIfNotExist() throws LedgerException{
        CouchDB couchDB = new CouchDB();
		String dbName = "julongchain";
		CouchDbClient db = creatConnectionDB();
        couchDB.createDatabaseIfNotExist(db, dbName);

    }

    /**
     * 获取数据库的配置信息
     * @throws LedgerException
     */
    @Test
    public void getDatabaseInfoTest() throws LedgerException{
        CouchDB couchDB = new CouchDB();
        CouchDbClient db = creatConnectionDB();
        CouchDbInfo info = couchDB.getDatabaseInfo(db);
        log.info(info.toString());
    }

    /**
     * 储存一个文档，存储内容包括id(唯一标识，不可重复)、rev、name、contentType、bytesIn（Attachment，数据类型为byte）
     * @throws Exception
     */
    @Test
    public void SaveDocTest() throws Exception{
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


    /**
     * 根据id和rev删除文档，id和rev可以拷贝库中数据
     * @throws Exception
     */
    @Test
    public void DeleteDocTest() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String id = "27b8df429dde46d5afe6a88917d08e80";
        String rev = "1-cbafc0fda20483668d9677c8f2ef65a6";
        String s = couchDB.deleteDoc(db, id, rev);
        log.info("test data",s);
    }

    /**
     * 删除某个数据库
     * @throws Exception
     */
    @Test
    public void DropDatabase() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String name = "";//此处为数据库名
        couchDB.dropDatabase(db, name);
    }

    /**
     * 根据id查询文档，查询结果为object,id可以拷贝库中任意doc的id
     * @throws Exception
     */
    @Test
    public void readDocTest() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String id = "3";//id可以拷贝库中任意doc的id
        JSONObject object = couchDB.readDoc(db, id);
        String rev = (String)object.get("_rev");//单独打印rev，测试读取是否成功
        log.info(object.toString());
    }

    /**
     * 根据id查询rev,id可以拷贝库中任意doc的id
     * @throws Exception
     */
    @Test
    public void getDocumentRevision() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String id = "3";//id可以拷贝库中任意doc的id
        String revision = couchDB.getDocumentRevision(db, id);
        log.info(revision);
    }

    /**
     * 根据两个id，查询两个id之间所有doc的信息,limit为条数（可以自己设定），skip默认为0
     * @throws Exception
     */
    @Test
    public void readDocRangeTest() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String startId = "1";
        String endId = "3";
        int limit = 2;
        int skip = 0;
        JSONObject object = couchDB.readDocRange(db, startId, endId, limit, skip);
        log.info(object.toString());
    }

    /**
     * 批量插入doc,参数为list<map>集合,参数可以自己设定
     * @throws Exception
     */
    @Test
    public void BatchUpdateDocuments() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        List<Object> list = new ArrayList();
        Map map1 = new HashMap();
        map1.put("asset_name", "marble01");
        map1.put("color", "red");
        map1.put("size", 2);
        map1.put("owner", "tom");

        Map map2 = new HashMap();
        map2.put("asset_name", "marble02");
        map2.put("color", "red");
        map2.put("size", 2);
        map2.put("owner", "zhangsan");
        list.add(map1);
        list.add(map2);
        List list1 = couchDB.BatchUpdateDocuments(db, list, true);
        log.info(list1.toString());
    }

    /**
     * 根绝查询语句查询doc，查询语句如下所示（为json格式）
     * @throws Exception
     */
    @Test
    public void queryDocuments() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String pm = "{\"selector\":{\"owner\":{\"$eq\":\"tom\"}},\"limit\":2}";
        List<Object> docs = couchDB.queryDocuments(db, pm, Object.class);
        log.info(docs.toString());
    }

    /**
     * 批量查询doc，查询条件为list（id）
     * @throws Exception
     */
    @Test
    public void BatchRetrieveDocumentMetadata() throws Exception{
        CouchDbClient db = creatConnectionDB();
        List<String> list = new ArrayList();
        list.add("marble01"); //id
        list.add("marble02");  //id
        CouchDB couchDB = new CouchDB();
        List list1 = couchDB.BatchRetrieveDocumentMetadata(db, list);
        log.info(list1.toString());
    }

    /**
     * 创建索引，条件为json结构.
     * @throws Exception
     */
    @Test
    public void CreatIndexTest() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        String indexDefSize = "{\"index\":{\"fields\":[{\"size\":\"desc\"}]},\"ddoc\":\"indexSizeSortDoc\", \"name\":\"indexSizeSortName\",\"type\":\"json\"}";
        Boolean aBoolean = couchDB.creatIndex(db, indexDefSize);
    }

    /**
     * 查询某个库中所有的index.
     * @throws Exception，查询结果为list
     */
    @Test
    public void ListIndexTest() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDB couchDB = new CouchDB();
        List list = couchDB.listIndex(db);
        log.info(list.toString());
    }

    /**
     * 获取couchdb的配置文件
     * @throws Exception
     */
    @Test
    public void cofigTest() throws Exception {
        NodeConfig config = NodeConfigFactory.getNodeConfig();
        NodeConfig.Ledger ledger = config.getLedger();
        NodeConfig.State state = ledger.getState();
        Map<String, String> couchDBConfig = state.getCouchDBConfig();
        String dbAddress = couchDBConfig.get("couchDBAddress");
        log.info(dbAddress);
    }

    /**
     * 系统库测试
     * @throws Exception
     */
    @Test
    public void CreateSystemDatabasesIfNotExistTest() throws Exception{
        CouchDbClient db = creatConnectionDB();
        CouchDBUtil.CreateSystemDatabasesIfNotExist(db);
    }
}
