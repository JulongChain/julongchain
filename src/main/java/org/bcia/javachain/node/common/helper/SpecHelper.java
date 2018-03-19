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
package org.bcia.javachain.node.common.helper;

import com.google.protobuf.ByteString;
import org.bcia.javachain.protos.node.Smartcontract;

import java.nio.charset.Charset;

/**
 * 对Spec对象的一些操作
 *
 * @author zhouhui
 * @date 2018/3/10
 * @company Dingxuan
 */
public class SpecHelper {

    private static final String DEFAULT_CHARSET = "UTF-8";

    public static Smartcontract.SmartContractInvocationSpec buildInvocationSpec(String smartContractName, String action, byte[] content) {
        //构造SmartContractInput对象
        Smartcontract.SmartContractInput.Builder inputBuilder = Smartcontract.SmartContractInput.newBuilder();
        inputBuilder.addArgs(ByteString.copyFrom(action, Charset.forName(DEFAULT_CHARSET)));
        inputBuilder.addArgs(ByteString.copyFrom(content));
        Smartcontract.SmartContractInput input = inputBuilder.build();

        //构造SmartContractSpec对象
        Smartcontract.SmartContractSpec.Builder specBuilder = Smartcontract.SmartContractSpec.newBuilder();
        specBuilder.setType(Smartcontract.SmartContractSpec.Type.JAVA);
        specBuilder.setSmartContractId(Smartcontract.SmartContractID.newBuilder().setName(smartContractName));
        specBuilder.setInput(input);
        Smartcontract.SmartContractSpec spec = specBuilder.build();

        //构造SmartContractInvocationSpec对象
        Smartcontract.SmartContractInvocationSpec.Builder invocationSpec = Smartcontract.SmartContractInvocationSpec
                .newBuilder();
        invocationSpec.setSmartContractSpec(spec);
        return invocationSpec.build();
    }


}
