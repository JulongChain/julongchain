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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.validator.valinternal;

import org.bcia.julongchain.common.exception.LedgerException;

/**
 * 内部验证器接口
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public interface InternalValidator {
    /**
     * 执行检验并准备更新包
     */
    PubAndHashUpdates validateAndPrepareBatch(Block block, boolean doMVCCValidation) throws LedgerException;
}
