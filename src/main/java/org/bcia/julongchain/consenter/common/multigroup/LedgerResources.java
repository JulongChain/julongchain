/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.consenter.common.multigroup;

import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;

/**
 * @author zhangmingyang
 * @Date: 2018/5/17
 * @company Dingxuan
 */
public class LedgerResources{
    private IMutableResources mutableResources;
    private ReadWriteBase readWriteBase;

    public LedgerResources(IMutableResources mutableResources, ReadWriteBase readWriteBase) {
        this.mutableResources = mutableResources;
        this.readWriteBase = readWriteBase;
    }

    public IMutableResources getMutableResources() {
        return mutableResources;
    }

    public void setMutableResources(IMutableResources mutableResources) {
        this.mutableResources = mutableResources;
    }

    public ReadWriteBase getReadWriteBase() {
        return readWriteBase;
    }

    public void setReadWriteBase(ReadWriteBase readWriteBase) {
        this.readWriteBase = readWriteBase;
    }
}
