/*
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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb;

import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;

import java.util.Arrays;

/**
 * 封装版本信息
 *
 * @author sunzongyu
 * @date 2018/4/9
 * @company Dingxuan
 */
public class VersionedValue {

    private LedgerHeight height;
    private byte[] value;

    public VersionedValue(){
    }

    public VersionedValue(LedgerHeight height, byte[] value){
        this.height = height;
        this.value = value;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VersionedValue that = (VersionedValue) o;
		return LedgerHeight.areSame(height, that.height) &&
				Arrays.equals(value, that.value);
	}

	public LedgerHeight getHeight() {
        return height;
    }

    public void setHeight(LedgerHeight height) {
        this.height = height;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
