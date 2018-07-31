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
package org.bcia.julongchain.node.common.helper;

import com.google.protobuf.ByteString;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.protos.node.SmartContractPackage;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 对Spec对象的一些操作
 *
 * @author zhouhui
 * @date 2018/3/10
 * @company Dingxuan
 */
public class SpecHelper {
    public static SmartContractPackage.SmartContractInvocationSpec buildInvocationSpec(String smartContractName, String action, byte[] content) {
        //构造SmartContractInput对象
        SmartContractPackage.SmartContractInput.Builder inputBuilder = SmartContractPackage.SmartContractInput.newBuilder();
        inputBuilder.addArgs(ByteString.copyFrom(action, Charset.forName(CommConstant.DEFAULT_CHARSET)));
        if (content != null) {
            inputBuilder.addArgs(ByteString.copyFrom(content));
        }
        SmartContractPackage.SmartContractInput input = inputBuilder.build();

        //构造SmartContractSpec对象
        SmartContractPackage.SmartContractSpec.Builder specBuilder = SmartContractPackage.SmartContractSpec.newBuilder();
        specBuilder.setType(SmartContractPackage.SmartContractSpec.Type.JAVA);
        specBuilder.setSmartContractId(SmartContractPackage.SmartContractID.newBuilder().setName(smartContractName));
        specBuilder.setInput(input);
        SmartContractPackage.SmartContractSpec spec = specBuilder.build();

        //构造SmartContractInvocationSpec对象
        SmartContractPackage.SmartContractInvocationSpec.Builder invocationSpec = SmartContractPackage.SmartContractInvocationSpec
                .newBuilder();
        invocationSpec.setSmartContractSpec(spec);
        return invocationSpec.build();
    }

    /**
     * 构造针对scName的执调用规格
     *
     * @param scName 要调用的智能合约名称
     * @param args   参数集合，由操作和参数列表组成
     * @return
     */
    public static SmartContractPackage.SmartContractInvocationSpec buildInvocationSpec(String scName, byte[]... args) {
        //构造SmartContractInput对象
        SmartContractPackage.SmartContractInput.Builder inputBuilder = SmartContractPackage.SmartContractInput.newBuilder();
        for (byte[] bytes : args) {
            if (bytes != null) {
                inputBuilder.addArgs(ByteString.copyFrom(bytes));
            } else {
                inputBuilder.addArgs(ByteString.copyFrom(new byte[0]));
            }
        }
        SmartContractPackage.SmartContractInput input = inputBuilder.build();

        //构造SmartContractSpec对象
        SmartContractPackage.SmartContractSpec.Builder specBuilder = SmartContractPackage.SmartContractSpec.newBuilder();
        specBuilder.setType(SmartContractPackage.SmartContractSpec.Type.JAVA);
        specBuilder.setSmartContractId(SmartContractPackage.SmartContractID.newBuilder().setName(scName));
        specBuilder.setInput(input);
        SmartContractPackage.SmartContractSpec spec = specBuilder.build();

        //构造SmartContractInvocationSpec对象
        SmartContractPackage.SmartContractInvocationSpec.Builder invocationSpecBuilder = SmartContractPackage.SmartContractInvocationSpec
                .newBuilder();
        invocationSpecBuilder.setSmartContractSpec(spec);
        return invocationSpecBuilder.build();
    }

    /**
     * 构造针对scName的执调用规格
     *
     * @param scName 要调用的智能合约名称
     * @param input
     * @return
     */
    public static SmartContractPackage.SmartContractInvocationSpec buildInvocationSpec(String scName,
                                                                                       SmartContractPackage.SmartContractInput input) {
        //构造SmartContractSpec对象
        SmartContractPackage.SmartContractSpec.Builder specBuilder = SmartContractPackage.SmartContractSpec.newBuilder();
        specBuilder.setType(SmartContractPackage.SmartContractSpec.Type.JAVA);
        specBuilder.setSmartContractId(SmartContractPackage.SmartContractID.newBuilder().setName(scName));
        if (input != null) {
            specBuilder.setInput(input);
        }
        SmartContractPackage.SmartContractSpec spec = specBuilder.build();

        //构造SmartContractInvocationSpec对象
        SmartContractPackage.SmartContractInvocationSpec.Builder invocationSpecBuilder = SmartContractPackage.SmartContractInvocationSpec
                .newBuilder();
        invocationSpecBuilder.setSmartContractSpec(spec);
        return invocationSpecBuilder.build();
    }

    /**
     * 构造针对scName的部署规格
     *
     * @param scName
     * @param scVersion
     * @param input
     * @return
     */
    public static SmartContractPackage.SmartContractDeploymentSpec buildDeploymentSpec(String scName, String scVersion,
                                                                                       String scPath, SmartContractPackage
                                                                                               .SmartContractInput input)
            throws NodeException {
        //构造SmartContractID对象
        SmartContractPackage.SmartContractID.Builder scIdBuilder = SmartContractPackage.SmartContractID.newBuilder();
        scIdBuilder.setName(scName);
        if (StringUtils.isNotBlank(scVersion)) {
            scIdBuilder.setVersion(scVersion);
        }
        if (StringUtils.isNotBlank(scPath)) {
            scIdBuilder.setPath(scPath);
        }
        SmartContractPackage.SmartContractID scId = scIdBuilder.build();

        //构造SmartContractSpec对象
        SmartContractPackage.SmartContractSpec.Builder specBuilder = SmartContractPackage.SmartContractSpec.newBuilder();
        specBuilder.setType(SmartContractPackage.SmartContractSpec.Type.JAVA);
        specBuilder.setSmartContractId(scId);
        if (input != null) {
            specBuilder.setInput(input);
        }
        SmartContractPackage.SmartContractSpec spec = specBuilder.build();

        //构造SmartContractDeploymentSpec对象
        SmartContractPackage.SmartContractDeploymentSpec.Builder deploymentSpec = SmartContractPackage.SmartContractDeploymentSpec
                .newBuilder();
        deploymentSpec.setSmartContractSpec(spec);

        if (StringUtils.isNotEmpty(scPath)) {
            try {
                Map<String, File> scFileMap = IoUtil.getFileRelativePath(scPath);
                byte[] tarBytes = IoUtil.tarWriter(scFileMap, 1024);
                byte[] codePackage = IoUtil.gzipWriter(tarBytes);
                deploymentSpec.setCodePackage(ByteString.copyFrom(codePackage));
            } catch (JavaChainException e) {
                throw new NodeException(e);
            }
        }

        return deploymentSpec.build();
    }
}