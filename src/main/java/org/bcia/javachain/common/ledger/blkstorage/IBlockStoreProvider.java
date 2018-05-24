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
package org.bcia.javachain.common.ledger.blkstorage;

import org.bcia.javachain.common.exception.LedgerException;

import java.util.List;

/**
 * 操作区块文件接口
 *
 * @author sunzongyu
 * @date 2018/4/7
 * @company Dingxuan
 */
public interface IBlockStoreProvider {

    IBlockStore createBlockStore(String ledgerid) throws LedgerException;

    IBlockStore openBlockStore(String ledgerid) throws LedgerException;

    Boolean exists(String ledgerid) throws LedgerException;

    List<String> list() throws LedgerException;

    void close() throws LedgerException;

}
