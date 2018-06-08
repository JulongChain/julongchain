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
package org.bcia.javachain.common.ledger.blkstorage;

import org.bcia.javachain.common.exception.LedgerException;

import java.util.List;

/**
 * 操作区块文件接口
 *
 * @author sunzongyu
 * @date 2018/4/7
 * @company Dingxuan
 */
public interface IBlockStoreProvider {

	/**
	 * 创建区块仓库
	 */
    IBlockStore createBlockStore(String ledgerID) throws LedgerException;

	/**
	 * 打开区块仓库
	 */
    IBlockStore openBlockStore(String ledgerID) throws LedgerException;

	/**
	 * 是否存在区块仓库
	 */
    Boolean exists(String ledgerID) throws LedgerException;

	/**
	 * 列出全部的区块仓库
	 */
	List<String> list() throws LedgerException;

	/**
	 * 关闭区块仓库
	 */
    void close() throws LedgerException;

}
