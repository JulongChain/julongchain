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
package org.bcia.julongchain.core.common.smartcontractprovider;

import com.google.protobuf.Message;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.protos.node.SmartContractDataPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * ISmartContractPackage encapsulates a smartcontract package which can be
 *    raw ChaincodeDeploymentSpec
 *    SignedChaincodeDeploymentSpec
 *  Attempt to keep the interface at a level with minimal
 *  interface for possible generalization.
 *
 * @author sunianle
 * @date 3/13/18
 * @company Dingxuan
 */
public interface ISmartContractPackage {
    //initFromBuffer initialize the package from bytes
    SmartContractDataPackage.SmartContractData initFromBuffer(byte[] buf) throws JavaChainException;
    //initFromFS gets the chaincode from the filesystem (includes the raw bytes too)
    SmartContractPackage.SmartContractDeploymentSpec initFromFS(String scName, String scVersion)throws JavaChainException;
    //putSmartcontractToFS writes the chaincode to the filesystem
    void putSmartcontractToFS() throws JavaChainException;
    //getDepSpec gets the SmartcontractDeploymentSpec from the package
    SmartContractPackage.SmartContractDeploymentSpec getDepSpec();
    //getDepSpecBytes gets the serialized SmartcontractDeploymentSpec from the package
    byte[] getDepSpecBytes();
    // ValidateSC validates and returns the chaincode deployment spec corresponding to
    // ChaincodeData. The validation is based on the metadata from ChaincodeData
    // One use of this method is to validate the chaincode before launching
    void validateSC(SmartContractDataPackage.SmartContractData scData) throws JavaChainException;
    //getPackageObject gets the object as a proto.Message
    Message getPackgeObject();
    // GetSmartContractData gets the SmartcontractData
    SmartContractDataPackage.SmartContractData getSmartContractData();
    //getId gets the fingerprint of the smartcontract based on package computation
    byte[] getId();

}
