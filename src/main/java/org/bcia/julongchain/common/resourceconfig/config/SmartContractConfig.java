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
package org.bcia.julongchain.common.resourceconfig.config;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.julongchain.common.resourceconfig.ResourcesConfigConstant;
import org.bcia.julongchain.common.resourceconfig.Validation;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.node.SmartContractDataPackage;
import org.bcia.julongchain.protos.node.ResourcesPackage;

/**
 * 智能合约配置对象
 *
 * @author zhouhui
 * @date 2018/4/19
 * @company Dingxuan
 */
public class SmartContractConfig implements ISmartContractDefinition {
    private String scName;
    private String scVersion;
    private String essc;
    private String vssc;
    private byte[] policy;
    private byte[] data;
    private byte[] id;
    private byte[] instantiationPolicy;
    private Validation validation;

    private ResourcesPackage.SmartContractIdentifier smartContractIdentifier;
    private ResourcesPackage.SmartContractValidation smartContractValidation;
    private ResourcesPackage.SmartContractEndorsement smartContractEndorsement;

    public SmartContractConfig(String scName, Configtx.ConfigTree tree) throws InvalidProtocolBufferException,
            ValidateException {
        if (tree != null && tree.getChildsCount() > 0) {
            throw new ValidateException("SmartContract does not support child");
        }

        this.scName = scName;

        if (tree != null && tree.getValuesMap() != null) {
            Configtx.ConfigValue identifierValue = tree.getValuesMap().get(ResourcesConfigConstant.SMART_CONTRACT_IDENTIFIER);
            if (identifierValue != null) {
                smartContractIdentifier = ResourcesPackage.SmartContractIdentifier.parseFrom(identifierValue.getValue());
                id = smartContractIdentifier.getHash().toByteArray();
                scVersion = smartContractIdentifier.getVersion();
            }

            Configtx.ConfigValue validationValue = tree.getValuesMap().get(ResourcesConfigConstant
                    .SMART_CONTRACT_VALIDATION);
            if (validationValue != null) {
                smartContractValidation = ResourcesPackage.SmartContractValidation.parseFrom(validationValue.getValue());

                this.vssc = smartContractValidation.getName();
                this.policy = smartContractValidation.getArgument().toByteArray();
                validation = new Validation(this.vssc, this.policy);
            }

            Configtx.ConfigValue endorsementValue = tree.getValuesMap().get
                    (ResourcesConfigConstant.SMART_CONTRACT_ENDORSEMENT);
            if (endorsementValue != null) {
                smartContractEndorsement = ResourcesPackage.SmartContractEndorsement.parseFrom(endorsementValue.getValue());
                this.essc = smartContractEndorsement.getName();
            }
        }
    }

    public SmartContractConfig(SmartContractDataPackage.SmartContractData data) {
        this.scName = data.getName();
        this.scVersion = data.getVersion();
        this.id = data.getId().toByteArray();
        this.essc = data.getEssc();
        this.vssc = data.getVssc();
        this.policy = data.getPolicy().toByteArray();
        this.validation = new Validation(this.vssc, this.policy);
    }

    @Override
    public String getSmartContractName() {
        return scName;
    }

    @Override
    public byte[] hash() {
        return id;
    }

    @Override
    public String getSmartContractVersion() {
        return scVersion;
    }

    @Override
    public Validation getValidation() {
        return validation;
    }

    @Override
    public String getEndorsement() {
        return essc;
    }
}
