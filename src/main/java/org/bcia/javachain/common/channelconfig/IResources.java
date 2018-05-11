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
package org.bcia.javachain.common.channelconfig;

import org.bcia.javachain.common.configtx.IValidator;
import org.bcia.javachain.common.policies.IManager;
import org.bcia.javachain.consenter.consensus.IOrderer;
import org.bcia.javachain.core.smartcontract.shim.helper.Channel;
import org.bcia.javachain.msp.IMspManager;
import org.bcia.javachain.protos.common.Configuration;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;

/**
 * Resources is the common set of config resources for all channels
 * Depending on whether chain is used at the orderer or at the peer, other
 * config resources may be available
 *
 * @author wanliangbing
 * @date 2018/3/15
 * @company Dingxuan
 */
public interface IResources {

    /** ConfigtxValidator returns the configtx.Validator for the channel
     *
     * @return
     */
    IValidator configtxValidator();

    /** PolicyManager returns the policies.Manager for the channel
     *
     * @return
     */
    IManager policyManager();

    /** ChannelConfig returns the config.Channel for the chain
     *
     */
    Channel channelConfig();

    /** OrdererConfig returns the config.Orderer for the channel
     * and whether the Orderer config exists
     * @return
     */
    IOrderer ordererConfig();

    /** ConsortiumsConfig() returns the config.Consortiums for the channel
     * and whether the consortiums config exists
     * @return
     */
    Configuration.Consortium consortiumsConfig();

    /** ApplicationConfig returns the configtxapplication.SharedConfig for the channel
     * and whether the Application config exists
     * @return
     */
    GenesisConfig.Application applicationConfig();

    /** MSPManager returns the msp.MSPManager for the chain
     *
     * @return
     */
    IMspManager mspManager();

    /** ValidateNew should return an error if a new set of configuration resources is incompatible with the current one
     *
     * @param resources
     */
    void validateNew(IResources resources);

}
