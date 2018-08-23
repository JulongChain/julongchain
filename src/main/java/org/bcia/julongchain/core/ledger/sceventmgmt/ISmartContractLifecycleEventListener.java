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
package org.bcia.julongchain.core.ledger.sceventmgmt;

import org.bcia.julongchain.common.exception.JulongChainException;

/**
 * 用于分类账本组件以及监听智能合约生命周期
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public interface ISmartContractLifecycleEventListener {

	/**
	 * 执行智能合约部署
	 */
    void handleSmartContractDeploy(SmartContractDefinition smartContractDefinition, byte[] dbArtifactsTar) throws JulongChainException;
}
