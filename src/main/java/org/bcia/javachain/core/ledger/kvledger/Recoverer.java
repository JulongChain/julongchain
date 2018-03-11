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
package org.bcia.javachain.core.ledger.kvledger;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class Recoverer {

    private Long firstBlockNum;
    private Recoverable recoverable;

    public Long getFirstBlockNum() {
        return firstBlockNum;
    }

    public void setFirstBlockNum(Long firstBlockNum) {
        this.firstBlockNum = firstBlockNum;
    }

    public Recoverable getRecoverable() {
        return recoverable;
    }

    public void setRecoverable(Recoverable recoverable) {
        this.recoverable = recoverable;
    }
}
