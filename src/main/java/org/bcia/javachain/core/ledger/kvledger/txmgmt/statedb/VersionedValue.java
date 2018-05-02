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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb;

import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;

/**
 * 封装版本信息
 *
 * @author sunzongyu
 * @date 2018/4/9
 * @company Dingxuan
 */
public class VersionedValue {

    private Height version;
    private byte[] value;

    public VersionedValue(){
    }

    public VersionedValue(Height version, byte[] value){
        this.version = version;
        this.value = value;
    }

    public Height getVersion() {
        return version;
    }

    public void setVersion(Height version) {
        this.version = version;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
