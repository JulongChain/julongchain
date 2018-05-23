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
package org.bcia.javachain.core.ledger.pvtdatastorage;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.core.ledger.PvtNsCollFilter;
import org.bcia.javachain.core.ledger.TxPvtData;

import java.util.List;

/**
 * pvt接口
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public interface IStore {
    void initLastCommitedBlock(long blockNum) throws LedgerException;

    List<TxPvtData> getPvtDataByBlockNum(long blockNum, PvtNsCollFilter filter) throws LedgerException;

    void prepare(long blockNum, List<TxPvtData> pvtData) throws LedgerException;

    void commit() throws LedgerException;

    void rollback() throws LedgerException ;

    boolean isEmpty() throws LedgerException ;

    long lastCommitedBlockHeight() throws LedgerException ;

    boolean hasPendingBatch() throws LedgerException ;

    void shutdown();
}
