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
package org.bcia.julongchain.common.groupconfig.config;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.GroupConfigConstant;
import org.bcia.julongchain.common.groupconfig.MSPConfigHandler;
import org.bcia.julongchain.common.groupconfig.capability.GroupProvider;
import org.bcia.julongchain.common.groupconfig.capability.IGroupCapabilities;
import org.bcia.julongchain.msp.IMspManager;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.common.Configuration;

import java.util.Iterator;
import java.util.Map;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/28
 * @company Dingxuan
 */
public class GroupConfig implements IGroupConfig {
    private Configuration.HashingAlgorithm protoHashingAlgorithm;
    private Configuration.BlockDataHashingStructure blockDataHashingStructure;
    private Configuration.ConsenterAddresses consenterAddresses;
    private Configuration.Consortium consortium;
    private Configuration.Capabilities capabilitiesProto;

    private IGroupCapabilities capabilities;

    private HashingAlgorithm hashingAlgorithm;

    private IMspManager mspManager;

    private IApplicationConfig applicationConfig;
    private IConsenterConfig consenterConfig;
    private IConsortiumsConfig consortiumsConfig;

    public GroupConfig(Configtx.ConfigTree groupTree) throws InvalidProtocolBufferException, ValidateException {
        if (groupTree != null && groupTree.getValuesMap() != null) {
            Configtx.ConfigValue hashingAlgorithmValue = groupTree.getValuesMap().get(GroupConfigConstant
                    .HASHING_ALGORITHM);
            if (hashingAlgorithmValue != null) {
                protoHashingAlgorithm = Configuration.HashingAlgorithm.parseFrom(hashingAlgorithmValue.getValue());
            }

            Configtx.ConfigValue blockDataHashingStructureValue = groupTree.getValuesMap().get(GroupConfigConstant.BLOCK_DATA_HASHING_STRUCTURE);
            if (blockDataHashingStructureValue != null) {
                blockDataHashingStructure = Configuration.BlockDataHashingStructure.parseFrom(blockDataHashingStructureValue.getValue());
            }

            Configtx.ConfigValue consenterAddressesValue = groupTree.getValuesMap().get(GroupConfigConstant.CONSENTER_ADDRESSES);
            if (consenterAddressesValue != null) {
                consenterAddresses = Configuration.ConsenterAddresses.parseFrom(consenterAddressesValue.getValue());
            }

            Configtx.ConfigValue consortiumValue = groupTree.getValuesMap().get(GroupConfigConstant.CONSORTIUM);
            if (consortiumValue != null) {
                consortium = Configuration.Consortium.parseFrom(consortiumValue.getValue());
            }

            Configtx.ConfigValue capabilitiesValue = groupTree.getValuesMap().get(GroupConfigConstant.CAPABILITIES);
            if (capabilitiesValue != null) {
                capabilitiesProto = Configuration.Capabilities.parseFrom(capabilitiesValue.getValue());
                capabilities = new GroupProvider(capabilitiesProto.getCapabilitiesMap());
            }

            validateHashingAlgorithm();
            validateBlockDataHashingStructure();
            validateConsenterAddresses();
        }

        MSPConfigHandler mspConfigHandler = new MSPConfigHandler(0);

        if (groupTree != null && groupTree.getChildsMap() != null) {
            Iterator<Map.Entry<String, Configtx.ConfigTree>> entries = groupTree.getChildsMap().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Configtx.ConfigTree> entry = entries.next();
                String childName = entry.getKey();
                Configtx.ConfigTree childTree = entry.getValue();

                if (GroupConfigConstant.APPLICATION.equals(childName)) {
                    applicationConfig = new ApplicationConfig(childTree, mspConfigHandler);
                }

                if (GroupConfigConstant.CONSENTER.equals(childName)) {
                    consenterConfig = new ConsenterConfig(childTree, mspConfigHandler);
                }

                if (GroupConfigConstant.CONSORTIUMS.equals(childName)) {
                    consortiumsConfig = new ConsortiumsConfig(childTree, mspConfigHandler);
                }
            }
        }

        mspManager = mspConfigHandler.createMSPManager();
    }

    public Configuration.HashingAlgorithm getProtoHashingAlgorithm() {
        return protoHashingAlgorithm;
    }

    public Configuration.BlockDataHashingStructure getBlockDataHashingStructure() {
        return blockDataHashingStructure;
    }

    @Override
    public Configuration.ConsenterAddresses getConsenterAddresses() {
        return consenterAddresses;
    }

    public Configuration.Consortium getConsortium() {
        return consortium;
    }

    @Override
    public IGroupCapabilities getCapabilities() {
        return capabilities;
    }

    public HashingAlgorithm getHashingAlgorithm() {
        return hashingAlgorithm;
    }

    @Override
    public IMspManager getMspManager() {
        return mspManager;
    }

    @Override
    public IApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    @Override
    public IConsenterConfig getConsenterConfig() {
        return consenterConfig;
    }

    @Override
    public IConsortiumsConfig getConsortiumsConfig() {
        return consortiumsConfig;
    }

    private void validateHashingAlgorithm() {
//        if(protoHashingAlgorithm.getName().equals())
        //TODO 校验哈希算法值
    }

    private void validateBlockDataHashingStructure() throws ValidateException {
        //TODO:
        if (blockDataHashingStructure != null && blockDataHashingStructure.getWidth() != Integer.MAX_VALUE) {
            throw new ValidateException("blockDataHashingStructure.width is wrong");
        }
    }

    private void validateConsenterAddresses() throws ValidateException {
        if (consenterAddresses != null && consenterAddresses.getAddressesCount() == 0) {
            throw new ValidateException("consenterAddresses.size can not be zero");
        }
    }



}
