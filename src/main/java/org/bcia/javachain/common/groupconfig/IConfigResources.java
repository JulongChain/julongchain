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

import org.bcia.javachain.common.configtx.IValidator;
import org.bcia.javachain.common.policies.IPolicyManager;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/27
 * @company Dingxuan
 */
public interface IConfigResources {
    IValidator configtxValidator();

    IPolicyManager getPolicyManager();



    // ConfigtxValidator returns the configtx.Validator for the channel
//    IConfigResources() configtx.Validator
//
//        // PolicyManager returns the policies.Manager for the channel
//    PolicyManager() policies.Manager
//
//        // ChannelConfig returns the config.Channel for the chain
//    ChannelConfig() Channel
//
//    // OrdererConfig returns the config.Orderer for the channel
//    // and whether the Orderer config exists
//    IConfigResources() (Orderer, bool)
//
//    // ConsortiumsConfig() returns the config.Consortiums for the channel
//    // and whether the consortiums config exists
//    IConfigResources() (Consortiums, bool)
//
//    // ApplicationConfig returns the configtxapplication.SharedConfig for the channel
//    // and whether the Application config exists
//    IConfigResources() (Application, bool)
//
//    // MSPManager returns the msp.MSPManager for the chain
//    IConfigResources() msp.MSPManager
//
//        // ValidateNew should return an error if a new set of configuration resources is incompatible with the current one
//    ValidateNew(resources Resources) error

}
