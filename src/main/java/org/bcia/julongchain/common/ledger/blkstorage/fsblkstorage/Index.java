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
package org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.protos.node.TransactionPackage;

/**
 * 索引接口
 * 用于对区块文件进行索引
 *
 * @author sunzongyu
 * @date 2018/4/9
 * @company Dingxuan
 */
public interface Index {

	/**
	 * 获取最新索引编号
	 */
    long getLastBlockIndexed() throws LedgerException;

	/**
	 * 对区块进行索引
	 */
	void indexBlock(BlockIndexInfo blockIndexInfo) throws LedgerException;

	/**
	 * 根据blockHash获取block位置
	 */
    FileLocPointer getBlockLocByHash(byte[] blockHash) throws LedgerException;

	/**
	 * 根据blockNum获取block位置
	 */
    FileLocPointer getBlockLocByBlockNum(long blockID) throws LedgerException;

	/**
	 * 根据交易ID获取交易位置
	 */
    FileLocPointer getTxLoc(String txID) throws LedgerException;

	/**
	 * 根据区块号和交易序号获取交易位置
	 */
    FileLocPointer getTXLocByBlockNumTranNum(long blockNum, long tranNum) throws LedgerException;

	/**
	 * 根据交易ID获取区块
	 */
    FileLocPointer getBlockLocByTxID(String txID) throws LedgerException;

	/**
	 * 根据交易ID获取交易验证号
	 */
    TransactionPackage.TxValidationCode getTxValidationCodeByTxID(String txID) throws LedgerException;

}
