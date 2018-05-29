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
package org.bcia.javachain.common.ledger.blockledger;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.ledger.blockledger.file.FileLedger;
import org.bcia.javachain.common.ledger.blockledger.file.FileLedgerFactory;
import org.bcia.javachain.protos.common.Common;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/29
 * @company Dingxuan
 */
public class UtilTest {
    String dir;
    String groupID;

    @Before
    public void before() throws Exception{
        dir = "/tmp/javachain/util";
        groupID = "myGroup";
        //重置目录
        System.out.println(deleteDir(new File(dir)));
    }

    @Test
    public void testCreateNextBlock() throws Exception{
        IFactory factory = new FileLedgerFactory(dir);
        IReader reader = factory.getOrCreate(groupID);
        Common.Block block = Util.createNextBlock(reader, new ArrayList<Common.Envelope>(){{
            add(Common.Envelope.newBuilder()
                    .setPayload(ByteString.copyFromUtf8("My Group"))
                    .build());
        }});
        Assert.assertNotNull(block);
        Assert.assertSame(block.getHeader().getNumber(), (long) 0);
    }

    @Test
    public void testGetBlock() throws Exception{
        IFactory factory = new FileLedgerFactory(dir);
        IReader reader = factory.getOrCreate(groupID);
        ((ReadWriteBase) reader).append(Util.createNextBlock(reader, null));
        Common.Block block = Util.getBlock(reader, 0);
        Assert.assertNotNull(block);
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

    private void soutBytes(byte[] bytes) throws Exception{
        int i = 0;
        for (byte aByte : bytes) {
            i++;
            System.out.print(aByte + "\t");
            if(i > 30){
                System.out.println();
                i = 0;
            }
        }
    }
}
