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
package org.bcia.julongchain.core.ledger.kvledger;

/**
 * 可恢复的数据库接口
 *
 * @author sunzongyu
 * @date 2018/4/9
 * @company Dingxuan
 */
public class Recoverer {

    private long firstBlockNum;
    private IRecoverable recoverable;

    public Recoverer(long firstBlockNum, IRecoverable recoverable) {
        this.firstBlockNum = firstBlockNum;
        this.recoverable = recoverable;
    }

    public long getFirstBlockNum() {
        return firstBlockNum;
    }

    public void setFirstBlockNum(long firstBlockNum) {
        this.firstBlockNum = firstBlockNum;
    }

    public IRecoverable getRecoverable() {
        return recoverable;
    }

    public void setRecoverable(IRecoverable recoverable) {
        this.recoverable = recoverable;
    }
}
