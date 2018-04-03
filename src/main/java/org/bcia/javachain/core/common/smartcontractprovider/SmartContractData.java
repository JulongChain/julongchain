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
package org.bcia.javachain.core.common.smartcontractprovider;

import org.bcia.javachain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.javachain.common.resourceconfig.Validation;
import org.bcia.javachain.core.node.NodeConfig;
import org.bcia.javachain.protos.node.Query;

/**
 * SmartContractData defines the datastructure for smartcontracts to be serialized by proto
 * Type provides an additional check by directing to use a specific package after instantiation
 * Data is Type specifc (see CDSPackage and SignedCDSPackage)
 *
 * @author sunianle
 * @date 3/13/18
 * @company Dingxuan
 */
public class SmartContractData implements ISmartContractDefinition {
    private Query.SmartContractInfo scInfo;

    private Validation scValidation;

    public SmartContractData(Query.SmartContractInfo scInfo) {
        this.scInfo = scInfo;

        scValidation = new Validation();
        scValidation.setMethod(scInfo.getVssc());
        //TODO:如何赋值
        scValidation.setArgs(new byte[0]);
    }

    @Override
    public String smartContractName() {
        return scInfo.getName();
    }

    @Override
    public byte[] hash() {
        return scInfo.getId().toByteArray();
    }

    @Override
    public String smartContractVersion() {
        return scInfo.getVersion();
    }

    @Override
    public Validation getValidation() {
        return scValidation;
    }

    @Override
    public String endorsement() {
        return scInfo.getEssc();
    }

    public byte[] marshal() {
        return null;
    }
}
