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
package org.bcia.javachain.common.ledger.blockledger.json;

import org.junit.After;
import org.junit.Before;

import java.io.File;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/28
 * @company Dingxuan
 */
public class JsonLedgerTest {
    @Before
    public void before() throws Exception{}

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
