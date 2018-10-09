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
package org.bcia.julongchain.core.commiter;

import org.bcia.julongchain.common.exception.CommitterException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.protos.common.Common;

import java.util.List;

/**
 * 提交者接口
 *
 * @author wanglei zhouhui
 * @date 2018/03/27
 * @company Dingxuan
 */
public interface ICommitter {

    void commitWithPrivateData(BlockAndPvtData blockAndPvtData) throws CommitterException, ValidateException;

    BlockAndPvtData getPrivateDataAndBlockByNum(long seqNumber) throws CommitterException;

    long getLedgerHeight() throws CommitterException;

    List<Common.Block> getBlocks(long[] blockSeqs);

    void close();
}
