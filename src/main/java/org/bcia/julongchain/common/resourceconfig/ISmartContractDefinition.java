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
package org.bcia.julongchain.common.resourceconfig;

/**
 * ISmartContractDefinition describes all of the necessary information for a peer to decide whether to endorse
 * a proposal and whether to validate a transaction, for a particular smart contract.
 *
 * @author wanliangbing
 * @date 2018/3/15
 * @company Dingxuan
 */
public interface ISmartContractDefinition {

    /**
     * the name of this smart contract (the name it was put in the SmartContractRegistry with).
     *
     * @return
     */
    String getSmartContractName();

    /**
     * the hash of the smart contract.
     *
     * @return
     */
    byte[] hash();

    /**
     * the version of the smart contract.
     *
     * @return
     */
    String getSmartContractVersion();

    /**
     * Validation returns how to validate transactions for this smart contract.
     * The string returned is the name of the validation method (usually 'vssc')
     * and the bytes returned are the argument to the validation (in the case of
     * 'vssc', this is a marshaled pb.VSSCArgs message).
     *
     * @return
     */
    Validation getValidation();

    /**
     * Endorsement returns how to endorse proposals for this smart contract.
     * The string returns is the name of the endorsement method (usually 'essc').
     *
     * @return
     */
    String getEndorsement();

}
