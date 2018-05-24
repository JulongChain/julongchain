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
package org.bcia.javachain.common.ledger.file;

import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.javachain.common.ledger.blockledger.file.FileLedgerFactory;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/24
 * @company Dingxuan
 */
public class FileLedgerTest {
    String dir;
    FileLedgerFactory fileLedgerFactory;
    ReadWriteBase fileLedger;
    @Before
    public void before() throws Exception{
        dir = "/tmp/javachain";
        //重置目录
        System.out.println(deleteDir(new File(dir)));
        //重新生成fileLedgerFactory
        fileLedgerFactory = new FileLedgerFactory(dir);
        //创建file ledger
        fileLedger = fileLedgerFactory.getOrCreate("myGroup");
    }

    @Test
    public void testGetOrCreate() throws Exception{
        Assert.assertNotNull(fileLedger);
        Assert.assertTrue(new File(dir).exists());
        Assert.assertSame(new File(dir).listFiles().length, 2);
        Assert.assertSame(fileLedger.height(), (long) 0);
        Assert.assertEquals(fileLedgerFactory.groupIDs().get(0), "myGroup");
    }

    @Test
    public void testAppend() throws Exception{
        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
        Common.Block block = factory.getGenesisBlock("myGroup");
        Assert.assertSame(new File(dir).length(), 0);
        fileLedger.append(block);
        Assert.assertNotSame(new File(dir).length(), 0);
    }

    @After
    public void after() throws Exception{}

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
