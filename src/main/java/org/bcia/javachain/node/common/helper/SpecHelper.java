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
import org.bcia.javachain.common.util.CommConstant;
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
    public static Smartcontract.SmartContractInvocationSpec buildInvocationSpec(String smartContractName, String action, byte[] content) {
        //构造SmartContractInput对象
        Smartcontract.SmartContractInput.Builder inputBuilder = Smartcontract.SmartContractInput.newBuilder();
        inputBuilder.addArgs(ByteString.copyFrom(action, Charset.forName(CommConstant.DEFAULT_CHARSET)));
        if (content != null) {
            inputBuilder.addArgs(ByteString.copyFrom(content));
        }
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

    /**
     * 构造针对scName的执行规格
     *
     * @param scName 要执行的智能合约名称
     * @param args   参数集合，由操作和参数列表组成
     * @return
     */
    public static Smartcontract.SmartContractInvocationSpec buildInvocationSpec(String scName, byte[]... args) {
        //构造SmartContractInput对象
        Smartcontract.SmartContractInput.Builder inputBuilder = Smartcontract.SmartContractInput.newBuilder();
        for (byte[] bytes : args) {
            inputBuilder.addArgs(ByteString.copyFrom(bytes));
        }
        Smartcontract.SmartContractInput input = inputBuilder.build();

        //构造SmartContractSpec对象
        Smartcontract.SmartContractSpec.Builder specBuilder = Smartcontract.SmartContractSpec.newBuilder();
        specBuilder.setType(Smartcontract.SmartContractSpec.Type.JAVA);
        specBuilder.setSmartContractId(Smartcontract.SmartContractID.newBuilder().setName(scName));
        specBuilder.setInput(input);
        Smartcontract.SmartContractSpec spec = specBuilder.build();

        //构造SmartContractInvocationSpec对象
        Smartcontract.SmartContractInvocationSpec.Builder invocationSpec = Smartcontract.SmartContractInvocationSpec
                .newBuilder();
        invocationSpec.setSmartContractSpec(spec);
        return invocationSpec.build();
    }


}
