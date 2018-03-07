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
package org.bcia.javachain.core.ssc;

import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContract;

/**
 * 系统智能合约接口
 *
 * @author sunianle
 * @date 2018/2/28
 * @company Dingxuan
 */
public interface ISystemSmartContract extends ISmartContract {
    //获取系统智能合约的名称
    String getSSCName();
    //获取系统智能合约的路径
    String getSSCPath();
    //获取初始化参数
    byte[][] getInitArgs();
    //是否可以通过发送proposal至节点来调用此系统合约
    boolean isInvokableExternal();
    //是否可以通过合约-合约(smartcontract-to-smartcontract)方式进行调用
    boolean isInvokaleSC2SC();
    //提供一个开关来启闭智能合约，这样可以不用从系统合约集合中删除合约，就可以使能/使不能系统智能合约
    boolean isEnabled();

}
