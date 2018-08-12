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
package org.bcia.julongchain.core.common.privdata;

/**
 * Filter defines a rule that filters peers according to data signed by them.
 * The Identity in the SignedData is a SerializedIdentity of a peer.
 *The Data is a message the peer signed, and the Signature is the corresponding
 * Signature on that Data.
 * Returns: True, if the policy holds for the given signed data.
 *         False otherwise
 *
 * @author sunianle
 * @date 4/27/18
 * @company Dingxuan
 */
public interface IFilter {

}
