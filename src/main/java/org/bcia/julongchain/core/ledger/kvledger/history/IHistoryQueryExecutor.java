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
package org.bcia.julongchain.core.ledger.kvledger.history;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;

/**
 * 历史查询器接口
 *
 * @author sunzongyu
 * @date 2018/04/25
 * @company Dingxuan
 */
public interface IHistoryQueryExecutor {

	/**
	 * 查询HistoryDB中数据
	 */
    IResultsIterator getHistoryForKey(String ns, String key) throws LedgerException;

	/**
	 * 查询key最新版本
	 */
	IResultsIterator getLastHistoryForKey(String ns, String key) throws LedgerException;
}
