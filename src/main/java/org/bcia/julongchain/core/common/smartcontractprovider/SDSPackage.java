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
import org.bcia.julongchain.protos.node.SmartContractDataPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;

import java.io.*;

/**
 * SDSPackage encapsulates SmartcontractDeploymentSpec.
 *
 * @author sunianle, sunzongyu
 * @date 4/25/18
 * @company Dingxuan
 */
public class SDSPackage implements ISmartContractPackage {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SDSPackage.class);

    private SmartContractPackage.SmartContractDeploymentSpec deploymentSpec;
    private byte[] buf;
    private SDSData data;
    private byte[] datab;
    private byte[] id;

    /**
     * 全部成员置为空
     */
    private void reset() {
        deploymentSpec = null;
        buf = null;
        datab = null;
        id = null;
    }

    /**
     * 根据buf进行初始化
     * @param buf
     * @return
     * @throws JavaChainException
     */
    @Override
    public SmartContractDataPackage.SmartContractData initFromBuffer(byte[] buf) throws JavaChainException {
        reset();

        SmartContractPackage.SmartContractDeploymentSpec deploymentSpec = null;
        try {
            deploymentSpec = SmartContractPackage.SmartContractDeploymentSpec.parseFrom(buf);
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new JavaChainException(e);
        }

        this.deploymentSpec = deploymentSpec;

        //TODO implement by sunzongyu, support for LSSC. date: 2018-05-08

        this.buf = buf;
        this.data = getSDSData(deploymentSpec);
        //TODO data->byte[], 序列化对象
        this.datab = IoUtil.obj2ByteArray(data);
        //TODO data.code + data.meta->byte[], 序列化对象
        this.id = IoUtil.obj2ByteArray(ArrayUtils.addAll(data.getCodeHash(), data.getMetaDataHash()));

        return getSmartContractData();
    }

    /**
     * 根据文件系统中文件进行初始化
     * @param scName
     * @param scVersion
     * @return
     * @throws JavaChainException
     */
    @Override
    public SmartContractPackage.SmartContractDeploymentSpec initFromFS(String scName, String scVersion) throws JavaChainException {
        //TODO implement by sunzongyu, support for LSSC. date: 2018-05-08
        reset();
        byte[] buf = SmartContractProvider.getSmartContractPackage(scName, scVersion);
        initFromBuffer(buf);
        return deploymentSpec;
    }

    /**
     * 根据sds结构解析SDSData
     * @param sds
     * @return
     */
    private SDSData getSDSData(SmartContractPackage.SmartContractDeploymentSpec sds) throws JavaChainException{
        //检查是否为空
        if(sds == null){
            throw new JavaChainException("Null sds");
        }
        SDSData sdsData = new SDSData();
        sdsData.setCodeHash(sds.getCodePackage().toByteArray());
        sdsData.setMetaDataHash(ArrayUtils.addAll(
                        sds.getSmartContractSpec().getSmartContractId().getName().getBytes(),
                        sds.getSmartContractSpec().getSmartContractId().getVersion().getBytes()));
        return sdsData;
    }

    /**
     * 将智能合约包存入文件系统
     * @throws JavaChainException
     */
    @Override
    public void putSmartcontractToFS() throws JavaChainException {
        //TODO implement by sunzongyu, support for LSSC. date: 2018-05-08
        //创建文件前的检查
        if(buf == null){
            throw new JavaChainException("Uninitialized package");
        }
        if(id == null){
            throw new JavaChainException("Id can not be null if buf is not null");
        }
        if(deploymentSpec == null){
            throw new JavaChainException("DeploymentSpec can not be null if buf is not null");
        }
        if(data == null){
            throw new JavaChainException("Null data");
        }
        if(datab == null){
            throw new JavaChainException("Null data bytes");
        }
        //创建文件
        String scName = deploymentSpec.getSmartContractSpec().getSmartContractId().getName();
        String scVersion = deploymentSpec.getSmartContractSpec().getSmartContractId().getVersion();
        String path = String.format("%s/%s.%s",SmartContractProvider.smartContractInstallPath, scName, scVersion);

        File file = new File(path);
        if(file.exists()){
            throw new JavaChainException("SmartContract " + path + " exists");
        }
        try {
            if (!IoUtil.createFileIfMissing(file.getPath())) {
                //创建文件失败
	            String errMsg = "File [" + path + "] can not be created. Please make sure you have the permission of the directory";
	            log.error(errMsg);
                throw new JavaChainException(errMsg);
            }
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw new JavaChainException(e);
        }
        //写入文件
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new JavaChainException(e);
        }
        try {
            os.write(buf);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new JavaChainException(e);
        }
    }

    @Override
    public SmartContractPackage.SmartContractDeploymentSpec getDepSpec() {
        if(this.deploymentSpec == null){
            throw new RuntimeException("Function getDepSpec() called on uninitialized package");
        }
        return this.deploymentSpec;
    }

    @Override
    public byte[] getDepSpecBytes() {
        if(this.buf == null){
            throw new RuntimeException("Function getDepSpecBytes() called on uninitialized package");
        }
        return this.buf;
    }

    @Override
    public void validateSC(SmartContractDataPackage.SmartContractData scData) throws JavaChainException {
        if(deploymentSpec == null){
            throw new JavaChainException("Uninitialized packag");
        }
        if(data == null){
            throw new JavaChainException("Null data");
        }
        if(!scData.getName().equals(deploymentSpec.getSmartContractSpec().getSmartContractId().getName()) ||
                !scData.getVersion().equals(deploymentSpec.getSmartContractSpec().getSmartContractId().getVersion())){
            throw new JavaChainException("Invalid smartcontract data failed");
        }
        SDSData otherData = (SDSData) IoUtil.byteArray2Obj(scData.getData().toByteArray());
        if(!data.equals(otherData)){
            throw new JavaChainException("Data mismatch");
        }
    }

    @Override
    public Message getPackgeObject() {
        return this.deploymentSpec;
    }

    @Override
    public SmartContractDataPackage.SmartContractData getSmartContractData() {
        if(deploymentSpec == null ||
                datab == null ||
                id == null){
            throw new RuntimeException("GetSmartContractData called on unitialized package");
        }
        SmartContractDataPackage.SmartContractData.Builder scDataBuilder = SmartContractDataPackage.SmartContractData
                .newBuilder();
        scDataBuilder.setName(deploymentSpec.getSmartContractSpec().getSmartContractId().getName());
        scDataBuilder.setVersion(deploymentSpec.getSmartContractSpec().getSmartContractId().getVersion());
        scDataBuilder.setData(ByteString.copyFrom(datab));
        scDataBuilder.setId(ByteString.copyFrom(id));

        return scDataBuilder.build();
    }

    @Override
    public byte[] getId() {
        if(this.id == null){
            throw new RuntimeException("Function getId() called on uninitialized package");
        }
        return this.id;
    }
}
