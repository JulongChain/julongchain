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
package org.bcia.julongchain.common.ledger.blkstorage;

/**
 * 提供索引前缀
 *
 * @author sunzongyu
 * @date 2018/3/7
 * @company Dingxuan
 */
public class BlockStorage {

    public static final String INDEXABLE_ATTR_BLOCK_NUM = "BlockNum";
    public static final String INDEXABLE_ATTR_BLOCK_HASH = "BlockHash";
    public static final String INDEXABLE_ATTR_TX_ID = "TxID";
    public static final String INDEXABLE_ATTR_BLOCK_NUM_TRAN_NUM = "BlockNumTranNum";
    public static final String INDEXABLE_ATTR_BLOCK_TX_ID = "BlockTxID";
    public static final String INDEXABLE_ATTR_TX_VALIDATION_CODE = "TxValidationCode";
}
