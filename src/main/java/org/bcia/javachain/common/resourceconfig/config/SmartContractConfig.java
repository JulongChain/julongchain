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
package org.bcia.javachain.common.resourceconfig.config;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.javachain.common.resourceconfig.ResourceConfigConstant;
import org.bcia.javachain.common.resourceconfig.Validation;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.peer.Resources;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/19
 * @company Dingxuan
 */
public class SmartContractConfig implements ISmartContractDefinition {
    String scName;
    private Resources.SmartContractIdentifier smartContractIdentifier;
    private Resources.SmartContractValidation smartContractValidation;
    private Resources.SmartContractEndorsement smartContractEndorsement;

    public SmartContractConfig(String scName, Configtx.ConfigTree tree) throws InvalidProtocolBufferException,
            ValidateException {
        if (tree != null && tree.getChildsCount() > 0) {
            throw new ValidateException("SmartContract does not support child");
        }

        this.scName = scName;

        if (tree != null && tree.getValuesMap() != null) {
            Configtx.ConfigValue identifierValue = tree.getValuesMap().get(ResourceConfigConstant.SMART_CONTRACT_IDENTIFIER);
            if (identifierValue != null) {
                smartContractIdentifier = Resources.SmartContractIdentifier.parseFrom(identifierValue.getValue());
            }

            Configtx.ConfigValue validationValue = tree.getValuesMap().get(ResourceConfigConstant
                    .SMART_CONTRACT_VALIDATION);
            if (validationValue != null) {
                smartContractValidation = Resources.SmartContractValidation.parseFrom(validationValue.getValue());
            }

            Configtx.ConfigValue endorsementValue = tree.getValuesMap().get
                    (ResourceConfigConstant.SMART_CONTRACT_ENDORSEMENT);
            if (endorsementValue != null) {
                smartContractEndorsement = Resources.SmartContractEndorsement.parseFrom(endorsementValue.getValue());
            }
        }

    }

    @Override
    public String getSmartContractName() {
        return scName;
    }

    @Override
    public byte[] hash() {
        return smartContractIdentifier.getHash().toByteArray();
    }

    @Override
    public String getSmartContractVersion() {
        return smartContractIdentifier.getVersion();
    }

    @Override
    public Validation getValidation() {
        return new Validation(smartContractValidation.getName(), smartContractValidation.getArgument().toByteArray());
    }

    @Override
    public String getEndorsement() {
        return smartContractEndorsement.getName();
    }
}
