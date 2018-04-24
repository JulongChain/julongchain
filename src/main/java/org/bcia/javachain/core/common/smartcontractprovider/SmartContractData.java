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

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.javachain.common.resourceconfig.Validation;
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
    //Name of the smartcontract
    private String name;
    //Version of the smartcontract
    private String version;
    //Essc name for the smartcotract instance
    private String essc;
    //Vssc name for the smartcotract instance
    private String vssc;
    //Policy endorsement policy for the smartcontract instance
    private byte[] policy;
    //Data data specific to the package
    private byte[] data;
    //Id of the chaincode that's the unique fingerprint for the CC
    //This is not currently used anywhere but serves as a good
    //eyecatcher
    private byte[] id;
    //instantiationPolicy for the smartcontract
    private byte[] instantiationPolicy;

    public SmartContractData(String name, String version, String essc, String vssc,
                             byte[] policy, byte[] data, byte[] id, byte[] instantiationPolicy) {
        this.name = name;
        this.version = version;
        this.essc = essc;
        this.vssc = vssc;
        this.policy = policy;
        this.data = data;
        this.id = id;
        this.instantiationPolicy = instantiationPolicy;
    }

    public SmartContractData(Query.SmartContractInfo scInfo) {
        this.name=scInfo.getName();
        this.version=scInfo.getVersion();
        this.essc=scInfo.getEssc();
        this.vssc=scInfo.getVssc();
        this.id=scInfo.getId().toByteArray();
        this.instantiationPolicy=null;
        this.policy=null;

    }

    public String getVssc() {
        return vssc;
    }

    public void setVssc(String vssc) {
        this.vssc = vssc;
    }

    public byte[] getPolicy() {
        return policy;
    }

    public void setPolicy(byte[] policy) {
        this.policy = policy;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getInstantiationPolicy() {
        return instantiationPolicy;
    }

    public void setInstantiationPolicy(byte[] instantiationPolicy) {
        this.instantiationPolicy = instantiationPolicy;
    }

    @Override
    public String getSmartContractName() {
        return this.name;
    }

    public void setSmartContractName(String name){
        this.name=name;
    }

    @Override
    public byte[] hash() {
        return this.id;
    }

    @Override
    public String getSmartContractVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public Validation getValidation(){
        Validation scValidation = new Validation();
        scValidation.setMethod(essc);
        scValidation.setArgs(policy);
        return scValidation;
    }

    @Override
    public String getEndorsement() {
        return essc;
    }

    public void setEndorsement(String essc) {
        this.essc = essc;
    }
}
