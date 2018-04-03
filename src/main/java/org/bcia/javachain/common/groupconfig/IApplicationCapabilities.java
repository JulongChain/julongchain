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
package org.bcia.javachain.common.groupconfig;

import org.bcia.javachain.common.exception.ValidateException;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/27
 * @company Dingxuan
 */
public interface IApplicationCapabilities {

    // Supported returns an error if there are unknown capabilities in this channel which are required
    void supported() throws ValidateException;

    // ForbidDuplicateTXIdInBlock specifies whether two transactions with the same TXId are permitted
    // in the same block or whether we mark the second one as TxValidationCode_DUPLICATE_TXID
    boolean forbidDuplicateTXIdInBlock();

    // ResourcesTree returns true if the peer should process the experimental resources transactions
    boolean resourcesTree();

    // PrivateChannelData returns true if support for private channel data (a.k.a. collections) is enabled.
    boolean privateGroupData();

    // V1_1Validation returns true is this channel is configured to perform stricter validation
    // of transactions (as introduced in v1.1).
    boolean v1_1Validation();
}
