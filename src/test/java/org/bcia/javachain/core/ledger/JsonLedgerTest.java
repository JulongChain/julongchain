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
package org.bcia.javachain.core.ledger;

import com.google.protobuf.util.JsonFormat;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/28
 * @company Dingxuan
 */
public class JsonLedgerTest {
    File file;

    @Test
    public void testWrite() throws Exception {
        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
        Common.Block block = factory.getGenesisBlock("MyGroup");
        file = new File("/home/bcia/testJsonFile/test");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(JsonFormat.printer().print(block));
        writer.flush();
        writer.close();
    }

    @Test
    public void testRead() throws Exception{
        long i = 1;
        int j = 1;
        System.out.println(i == j);
        file = new File("/home/bcia/testJsonFile/test1");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuffer buffer = new StringBuffer("");
        String s = "";
        while((s = reader.readLine()) != null){
            buffer.append(s + "\n");
        }
        System.out.println(buffer.toString());
    }
}
