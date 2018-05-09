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
package org.bcia.javachain.common.groupconfig.capability;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/27
 * @company Dingxuan
 */
public interface IConsenterCapabilities {
    // PredictableChannelTemplate specifies whether the v1.0 undesirable behavior of setting the /Channel
    // group's mod_policy to "" and copy versions from the orderer system channel config should be fixed or not.
    boolean predictableGroupTemplate();

    // Resubmission specifies whether the v1.0 non-deterministic commitment of tx should be fixed by re-submitting
    // the re-validated tx.
    boolean resubmission();

    // Supported returns an error if there are unknown capabilities in this channel which are required
    void supported();

    // ExpirationCheck specifies whether the orderer checks for identity expiration checks
    // when validating messages
    boolean expirationCheck();
}
