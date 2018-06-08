/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/
package org.bcia.julongchain.core.smartcontract.shim.impl;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IKeyValue;
import org.bcia.julongchain.protos.ledger.queryresult.KvQueryResult;

class KeyValue implements IKeyValue {

	private final String key;
	private final ByteString value;

	KeyValue(KvQueryResult.KV kv) {
		this.key = kv.getKey();
		this.value = kv.getValue();
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public byte[] getValue() {
		return value.toByteArray();
	}

	@Override
	public String getStringValue() {
		return value.toStringUtf8();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		KeyValue other = (KeyValue) obj;
		if (key == null) {
			if (other.key != null) return false;
		} else if (!key.equals(other.key)) return false;
		if (value == null) {
			if (other.value != null) return false;
		} else if (!value.equals(other.value)) return false;
		return true;
	}

}
