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
package org.bcia.julongchain.core.commiter;

import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.TransactionPackage;

/**
 * VSSC校验器接口定义
 *
 * @author zhouhui
 * @date 2018/05/23
 * @company Dingxuan
 */
public interface IVsscValidator {
    TransactionPackage.TxValidationCode vsscValidateTx(Common.GroupHeader groupHeader,
                                                       ProposalPackage.SmartContractHeaderExtension extension,
                                                       byte[] envelopeBytes,
                                                       ProposalResponsePackage.ProposalResponsePayload
                                                               proposalResponsePayload);
}
