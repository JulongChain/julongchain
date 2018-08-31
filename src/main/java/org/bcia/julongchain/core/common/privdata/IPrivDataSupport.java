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
package org.bcia.julongchain.core.common.privdata;

import org.bcia.julongchain.common.exception.PrivDataException;
import org.bcia.julongchain.core.ledger.IQueryExecutor;
import org.bcia.julongchain.msp.IIdentityDeserializer;
import org.bcia.julongchain.protos.common.Collection;

/**
 * 抽象的私有数据辅助
 * @author sunianle, sunzongyu1
 * @date 3/15/18
 * @company Dingxuan
 */
public interface IPrivDataSupport {
    /**
	 * 获取查询器
     */
    IQueryExecutor getQueryExecutorForLedger(String groupID) throws PrivDataException;

    /**
     * 获取给出集合的key
     */
    String getCollectionKVSKey(Collection.CollectionCriteria cc);

    /**
	 * 获取并行实体
     */
    IIdentityDeserializer getIdentityDeserializer(String groupID);

	/**
	 * 构建集合的key
	 */
	String buildCollectionKVSKey(String smartContractname);

	/**
	 * 判断给出key是否为集合key
	 */
    boolean isCollectionConfigKey(String key);
}
