/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.statecouchdb;

import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.CompositeKey;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;

import java.util.HashMap;
import java.util.Map;

/**
 * 包含committedVersions和revisionNumbers
 * 处理块过程中用于本地缓存
 * committedVersions 用于验证读集合
 * revisionNumbers   用于批量提交阶段
 *
 * @author sunzongyu
 * @date 2018/05/22
 * @company Dingxuan
 */
public class CommittedVersions {
	private Map<CompositeKey, LedgerHeight> committedVersions = new HashMap<>(32);
	private Map<CompositeKey, String> revisionNumbers = new HashMap<>(32);

	public Map<CompositeKey, String> getRevisionNumbers() {
		return revisionNumbers;
	}

	public void setRevisionNumbers(Map<CompositeKey, String> revisionNumbers) {
		this.revisionNumbers = revisionNumbers;
	}

	public Map<CompositeKey, LedgerHeight> getCommittedVersions() {
		return committedVersions;
	}

	public void setCommittedVersions(Map<CompositeKey, LedgerHeight> committedVersions) {
		this.committedVersions = committedVersions;
	}
}
