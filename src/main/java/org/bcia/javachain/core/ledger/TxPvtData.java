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

import org.bcia.javachain.protos.ledger.rwset.Rwset;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/27
 * @company Dingxuan
 */
public class TxPvtData {

    private Long seqInBlock;
    private Rwset.TxPvtReadWriteSet writeSet;

    public Long getSeqInBlock() {
        return seqInBlock;
    }

    public void setSeqInBlock(Long seqInBlock) {
        this.seqInBlock = seqInBlock;
    }

    public Rwset.TxPvtReadWriteSet getWriteSet() {
        return writeSet;
    }

    public void setWriteSet(Rwset.TxPvtReadWriteSet writeSet) {
        this.writeSet = writeSet;
    }
}
