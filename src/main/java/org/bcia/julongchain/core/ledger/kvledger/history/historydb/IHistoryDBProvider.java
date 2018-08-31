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
package org.bcia.julongchain.core.ledger.kvledger.history.historydb;

import org.bcia.julongchain.common.exception.LedgerException;

/**
 * 历史数据库服务提供者借口
 *
 * @author sunzongyu1
 * @date 2018/3/9
 * @company Dingxuan
 */
public interface IHistoryDBProvider {

    /**
     * 获取History db
     */
    IHistoryDB getDBHandle(String id) throws LedgerException;

	/**
	 * 关闭
	 */
	void close() throws LedgerException;

}
