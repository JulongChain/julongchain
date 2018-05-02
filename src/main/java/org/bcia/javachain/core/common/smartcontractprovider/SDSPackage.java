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

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.node.SmartContractDataPackage;
import org.bcia.javachain.protos.node.Smartcontract;

/**
 * 类描述
 *
 * @author sunianle
 * @date 4/25/18
 * @company Dingxuan
 */
public class SDSPackage implements ISmartContractPackage {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SDSPackage.class);

    private Smartcontract.SmartContractDeploymentSpec deploymentSpec;

    private void reset() {

    }

    @Override
    public SmartContractDataPackage.SmartContractData initFromBuffer(byte[] buf) throws JavaChainException {
        reset();

        Smartcontract.SmartContractDeploymentSpec deploymentSpec = null;
        try {
            deploymentSpec = Smartcontract.SmartContractDeploymentSpec.parseFrom(buf);
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new JavaChainException(e);
        }

        this.deploymentSpec = deploymentSpec;

        //TODO:逻辑未完全实现，先保证可以测试通过


        return null;
    }

    @Override
    public Smartcontract.SmartContractDeploymentSpec initFromFS(String scName, String scVersion) throws JavaChainException {
        return null;
    }

    @Override
    public void putSmartcontractToFS() throws JavaChainException {

    }

    @Override
    public Smartcontract.SmartContractDeploymentSpec getDepSpec() {
        return deploymentSpec;
    }

    @Override
    public byte[] getDepSpecBytes() {
        return new byte[0];
    }

    @Override
    public void validateSC(SmartContractDataPackage.SmartContractData scData) throws JavaChainException {

    }

    @Override
    public Message getPackgeObject() {
        return null;
    }

    @Override
    public SmartContractDataPackage.SmartContractData getSmartContractData() {
        SmartContractDataPackage.SmartContractData.Builder scDataBuilder = SmartContractDataPackage.SmartContractData
                .newBuilder();
        scDataBuilder.setName(deploymentSpec.getSmartContractSpec().getSmartContractId().getName());
        scDataBuilder.setVersion(deploymentSpec.getSmartContractSpec().getSmartContractId().getVersion());

        //TODO:逻辑未完全实现，先保证可以测试通过

        return scDataBuilder.build();
    }

    @Override
    public byte[] getId() {
        return new byte[0];
    }
}
