/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.javachain.core.ledger;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDbProvider;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.javachain.core.ledger.util.Util;
import org.iq80.leveldb.DBIterator;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/02
 * @company Dingxuan
 */
public class LedterTest {
    @Test
    public void longl() throws Throwable {
//        LevelDbProvider provider = LevelDbProvider.newProvider("/tmp/fabric/ledgertests/ledgermgmt/ledgersData/ledgerProvider");
        LevelDbProvider provider = LevelDbProvider.newProvider("/home/bcia/javachain/ledgersData/ledgerProvider");
        Iterator<Map.Entry<byte[], byte[]>> itr =  provider.getIterator(null, null);
        while(itr.hasNext()){
            Map.Entry<byte[], byte[]> entry = itr.next();
            System.out.println(new String(entry.getKey()));
            System.out.println(new String(entry.getValue()));
            System.out.println("_____________________________________");
        }
    }
}
