/**
 * Copyright Turing. All Rights Reserved.
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

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.DB;

/**
 * 提供操作couchdb的操作方法，如增，删，改，查
 *
 * @author zhangdazhi
 * @date 2018/04/20
 * @company Turing
 */
public class CouchDBFactory {

    private static JavaChainLog log = JavaChainLogFactory.getLog(CouchDBFactory.class);

   public static DB getDB() throws LedgerException{
       return null;
   }

}
