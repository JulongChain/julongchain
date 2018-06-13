/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.core.common.smartcontractprovider;

import java.io.Serializable;
import java.util.Arrays;

/**
 * CDSData is data stored in the LSCC on instantiation of a CC
 * for CDSPackage.  This needs to be serialized for ChaincodeData
 * hence the protobuf format
 *
 * @author sunzongyu
 * @date 2018/05/08
 * @company Dingxuan
 */
public class SDSData implements Serializable {
    //CodeHash hash of CodePackage from ChaincodeDeploymentSpec
    private byte[] codeHash = "protobuf:\"bytes,1,opt,name=codehash,proto3\"".getBytes();

    //MetaDataHash hash of Name and Version from ChaincodeDeploymentSpec
    private byte[] metaDataHash = "protobuf:\"bytes,2,opt,name=metadatahash,proto3\"".getBytes();

    public void reset(){
        //nothing to do
    }

    public void protoMessage(){
        //nothing to do
    }

    @Override
    public String toString() {
        return "codeHash: " + new String(codeHash) + "\nmetaHash: " + new String(metaDataHash);
    }

    @Override
    public boolean equals(Object obj) {
        return  obj instanceof SDSData &&
                Arrays.equals(codeHash, ((SDSData) obj).getCodeHash()) &&
                Arrays.equals(metaDataHash, ((SDSData) obj).getMetaDataHash());
    }

    public byte[] getCodeHash() {
        return codeHash;
    }

    public void setCodeHash(byte[] codeHash) {
        this.codeHash = codeHash;
    }

    public byte[] getMetaDataHash() {
        return metaDataHash;
    }

    public void setMetaDataHash(byte[] metaDataHash) {
        this.metaDataHash = metaDataHash;
    }
}
