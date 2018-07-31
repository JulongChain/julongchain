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
package org.bcia.julongchain.core.ledger.pvtdatastorage;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.ledger.PvtNsCollFilter;
import org.bcia.julongchain.core.ledger.TxPvtData;

import java.util.List;

/**
 * pvt接口
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public interface IPvtDataStore {
	/**
	 * 在pvtDataStore中设置最后提交的blockNum
	 */
	void initLastCommittedBlock(long blockNum) throws LedgerException;

	/**
	 * 通过blockNum获取pvtData
	 */
    List<TxPvtData> getPvtDataByBlockNum(long blockNum, PvtNsCollFilter filter) throws LedgerException;

	/**
	 * 向pvtDataStore中提交数据并进入预备状态（batchPending）
	 * @param blockNum 区块编号
	 * @param pvtData 提交的pvtData
	 * @throws LedgerException 	1.当pvtDataStore已经进入预备状态时
	 * 							2.当提交的区块号不是当前区块号+1时
	 */
    void prepare(long blockNum, List<TxPvtData> pvtData) throws LedgerException;

	/**
	 * 完成向pvtDataStore提交数据
	 * 解除预备状态
	 * 设置完成标志
	 * @throws LedgerException	1.当pvtDataStore没有进入预备状态时
	 */
	void commit() throws LedgerException;

	/**
	 * 回滚操作
	 * 删除预备标志之前的数据
	 * @throws LedgerException	1.当pvtDataStore没有进入预备状态时
	 */
    void rollback() throws LedgerException ;

	/**
	 * 是否为空
	 */
	boolean isEmpty() throws LedgerException ;

	/**
	 * 高度
	 */
    long lastCommitedBlockHeight() throws LedgerException ;

	/**
	 * 是否进入预备状态
	 */
	boolean hasPendingBatch() throws LedgerException ;

	/**
	 * 关闭
	 * 暂时不需要进行操作
	 */
    void shutdown();
}
