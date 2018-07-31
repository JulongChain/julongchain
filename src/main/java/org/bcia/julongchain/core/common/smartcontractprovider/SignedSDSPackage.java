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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.SmartContractDataPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.bcia.julongchain.protos.node.SignedScDepSpec;

import java.io.*;

/**
 * SignedSDSPackage encapsulates SignedSmartContractDeploymentSpec.
 *
 * @author sunianle, sunzongyu
 * @date 4/25/18
 * @company Dingxuan
 */
public class SignedSDSPackage implements ISmartContractPackage {
    private static final JavaChainLog log = JavaChainLogFactory.getLog(SignedSDSPackage.class);

    private byte[] buf;
    private SmartContractPackage.SmartContractDeploymentSpec depSpec;
    private SignedScDepSpec.SignedSmartContractDeploymentSpec sDepSpec;
    private Common.Envelope env;
    private SignedSDSData data;
    private byte[] datab;
    private byte[] id;

    @Override
    public SmartContractDataPackage.SmartContractData initFromBuffer(byte[] buf) throws JavaChainException {
        reset();
        this.buf = buf;
        try {
            this.env = Common.Envelope.parseFrom(buf);
        } catch (InvalidProtocolBufferException e) {
            log.error("Fail to unmarshal envelope form bytes");
            throw new JavaChainException(e);
        }
        Common.GroupHeader gh = SmartContractPackageUtil.extractGroupHeaderFromEnvelope(env);
        if(gh.getType() != Common.HeaderType.SMART_CONTRACT_PACKAGE_VALUE){
            throw new JavaChainException("Invalid type of envelope for smartcontract package");
        }
        this.sDepSpec = SmartContractPackageUtil.extractSignedSmartContractDeploymentSpecFromEnvelope(env);
        try {
            this.depSpec = SmartContractPackage.SmartContractDeploymentSpec.parseFrom(sDepSpec.getSmartContractDeploymentSpec());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error getting deployment spec");
            throw new JavaChainException(e);
        }
        this.data = getSDSData(sDepSpec);
        //TODO data -> byte[]
        this.datab = IoUtil.obj2ByteArray(data);
        //TODO data.codehash + data.metadatahash + data.signaturehash -> byte[]
        byte[] totalHash = new byte[0];
        totalHash = ArrayUtils.addAll(totalHash, data.getCodeHash());
        totalHash = ArrayUtils.addAll(totalHash, data.getMetaDataHash());
        totalHash = ArrayUtils.addAll(totalHash, data.getSignatureHash());
        this.id = IoUtil.obj2ByteArray(totalHash);
        return getSmartContractData();
    }

    @Override
    public SmartContractPackage.SmartContractDeploymentSpec initFromFS(String scName, String scVersion) throws JavaChainException {
        reset();
        byte[] buf = SmartContractProvider.getSmartContractPackage(scName, scVersion);
        initFromBuffer(buf);
        return depSpec;
    }

    @Override
    public void putSmartcontractToFS() throws JavaChainException {
        if(buf == null){
            throw new JavaChainException("Uninitialized package");
        }
        if(id == null){
            throw new JavaChainException("Id cannot be null if buf is not null");
        }
        if(sDepSpec == null || depSpec == null){
            throw new JavaChainException("Depspec cannot be null if buf is not null");
        }
        if(env == null){
            throw new JavaChainException("Env cannot be null if buf is not null");
        }
        if(data == null){
            throw new JavaChainException("Null data");
        }
        if(datab == null){
            throw new JavaChainException("Null datab");
        }

        String scName = depSpec.getSmartContractSpec().getSmartContractId().getName();
        String scVersion = depSpec.getSmartContractSpec().getSmartContractId().getVersion();
        String path = String.format("%s/%s.%s", SmartContractProvider.smartContractInstallPath, scName, scVersion);

        File file = new File(path);
        if(file.exists()){
            throw new JavaChainException(String.format("SmartContract %s is exists", path));
        }
        try {
            IoUtil.createFileIfMissing(path);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JavaChainException(e);
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        try {
            os.write(buf);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new JavaChainException(e);
        }
    }

    @Override
    public byte[] getId() {
        if(id == null){
            throw new RuntimeException("GetId called on uninitialized package");
        }
        return id;
    }

    @Override
    public SmartContractPackage.SmartContractDeploymentSpec getDepSpec(){
        if(depSpec == null){
            throw new RuntimeException("GetDepSpec called on uninitialized package");
        }
        return depSpec;
    }

    public byte[] getInstantiationPolicy() throws JavaChainException {
        if(sDepSpec == null){
            throw new JavaChainException("GetInstantiationPolicy called on uninitialized package");
        }
        return sDepSpec.getInstantiationPolicy().toByteArray();
    }

    @Override
    public byte[] getDepSpecBytes() {
        if(sDepSpec == null || sDepSpec.getSmartContractDeploymentSpec() == null){
            throw new RuntimeException("GetDepSpecBytes called on uninitialized package");
        }
        return sDepSpec.getSmartContractDeploymentSpec().toByteArray();
    }

    @Override
    public Message getPackgeObject() {
        return env;
    }

    @Override
    public SmartContractDataPackage.SmartContractData getSmartContractData() {
        if(depSpec == null || datab == null){
            throw new RuntimeException("GetSmartContractData called on uninitialized package");
        }
        byte[] instPolicy = new byte[0];
        if(sDepSpec != null){
            instPolicy = sDepSpec.getInstantiationPolicy().toByteArray();
        }
        return SmartContractDataPackage.SmartContractData.newBuilder()
                .setName(depSpec.getSmartContractSpec().getSmartContractId().getName())
                .setVersion(depSpec.getSmartContractSpec().getSmartContractId().getVersion())
                .setData(ByteString.copyFrom(datab))
                .setId(ByteString.copyFrom(instPolicy))
                .setInstantiationPolicy(ByteString.copyFrom(instPolicy))
                .build();
    }

    private SignedSDSData getSDSData(SignedScDepSpec.SignedSmartContractDeploymentSpec ssds) throws JavaChainException{
        if(ssds == null){
            log.error("Null sds");
            return null;
        }
        SmartContractPackage.SmartContractDeploymentSpec sds = null;
        try {
            sds = SmartContractPackage.SmartContractDeploymentSpec.parseFrom(ssds.getSmartContractDeploymentSpec());
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw new JavaChainException(e);
        }
        //组装signedSDSData
        SignedSDSData ssdsData = new SignedSDSData();
        ssdsData.setCodeHash(sds.getCodePackage().toByteArray());
        ssdsData.setMetaDataHash(ArrayUtils.addAll(
                sds.getSmartContractSpec().getSmartContractId().getName().getBytes(),
                sds.getSmartContractSpec().getSmartContractId().getVersion().getBytes()
        ));
        if(ssds.getInstantiationPolicy() == null){
            log.error(String.format("instantiation policy can not be null for smartcontract (%s:%s)",
                    sds.getSmartContractSpec().getSmartContractId().getName(),
                    sds.getSmartContractSpec().getSmartContractId().getVersion()
            ));
            return null;
        }
        byte[] signatureHash = new byte[0];
        for(ProposalResponsePackage.Endorsement o : ssds.getOwnerEndorsementsList()){
            signatureHash = ArrayUtils.addAll(signatureHash, o.getEndorser().toByteArray());
        }
        ssdsData.setSignatureHash(signatureHash);
        return ssdsData;
    }

    @Override
    public void validateSC(SmartContractDataPackage.SmartContractData scData) throws JavaChainException {
        if(sDepSpec == null){
            throw new JavaChainException("Uninitialized package");
        }
        if(sDepSpec.getSmartContractDeploymentSpec() == null){
            throw new JavaChainException("Signed smartcontract deployment spec cannot be null in a package");
        }
        if(depSpec == null){
            throw new JavaChainException("Smartcontract deployment spec cannot be null in a package");
        }
        if(!scData.getName().equals(depSpec.getSmartContractSpec().getSmartContractId().getName()) ||
                !scData.getVersion().equals(depSpec.getSmartContractSpec().getSmartContractId().getVersion())){
            throw new JavaChainException("Invalid smartcontract data");
        }
        SignedSDSData otherData = (SignedSDSData) IoUtil.byteArray2Obj(scData.getData().toByteArray());
        if(!data.equals(otherData)){
            throw new JavaChainException("Data mismatch");
        }
    }

    private void reset(){
        this.buf = new byte[0];
        this.depSpec = SmartContractPackage.SmartContractDeploymentSpec.getDefaultInstance();
        this.sDepSpec = SignedScDepSpec.SignedSmartContractDeploymentSpec.getDefaultInstance();
        this.env = Common.Envelope.getDefaultInstance();
        this.data = new SignedSDSData();
        this.datab = new byte[0];
        this.id = new byte[0];
    }
}
