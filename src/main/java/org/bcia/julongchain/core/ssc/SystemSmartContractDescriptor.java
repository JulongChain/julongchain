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
package org.bcia.julongchain.core.ssc;

import org.bcia.julongchain.core.smartcontract.shim.ISmartContract;

/**
 * 系统智能合约的描述类
 *
 * @author sunianle, sunzongyu1
 * @date 2018/2/28
 * @company Dingxuan
 */
public class SystemSmartContractDescriptor{
    private String sscName;
    private String sscPath;
    private String[] initArgs;
    private boolean bInvokableExternal;
    private boolean bInvokaleSC2SC;
    private boolean bEnabled;
    private String sscVersion;
    private ISmartContract smartContract;

    public SystemSmartContractDescriptor(String sscName,
                                         String sscPath,
										 String sscVersion,
										 String[] initArgs,
                                         boolean bInvokableExternal,
                                         boolean bInvokaleSC2SC,
                                         boolean bEnabled) {
        this.sscName = sscName;
        this.sscPath = sscPath;
        this.sscVersion = sscVersion == null ? "development build": sscVersion;
        this.initArgs = initArgs;
        this.bInvokableExternal = bInvokableExternal;
        this.bInvokaleSC2SC = bInvokaleSC2SC;
        this.bEnabled = bEnabled;
    }


    //获取系统智能合约的名称
    public String getSSCName(){
       return this.sscName;
    }

    //获取系统智能合约的路径
    public String getSSCPath(){
        return this.sscPath;
    }

    //获取初始化参数
    public String[] getInitArgs(){
        return this.initArgs;
    }

    //是否可以通过发送proposal至节点来调用此系统合约
    public boolean isInvokableExternal(){
        return this.bInvokableExternal;
    }

    //是否可以通过合约-合约(smartcontract-to-smartcontract)方式进行调用
    boolean isInvokaleSC2SC(){
        return this.bInvokaleSC2SC;
    }

    //提供一个开关来启闭智能合约，这样可以不用从系统合约集合中删除合约，就可以使能/使不能系统智能合约
    public boolean isEnabled(){
        return this.bEnabled;
    }

	public ISmartContract getSmartContract() {
		return smartContract;
	}

	//获取系统智能合约版本
	public String getSSCVersion() {
    	return sscVersion;
	}

	public void setSmartContract(ISmartContract smartContract) {
		this.smartContract = smartContract;
	}
}
