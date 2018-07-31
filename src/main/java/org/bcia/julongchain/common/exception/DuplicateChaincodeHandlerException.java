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
package org.bcia.julongchain.common.exception;


import org.bcia.julongchain.core.smartcontract.Handler;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/07/24
 * @company Dingxuan
 */
public class DuplicateChaincodeHandlerException extends JavaChainException {
	private static final String MODULE_NAME = "[DuplicateChaincodeHandler]";
	private SmartContractPackage.SmartContractID scID;

	public DuplicateChaincodeHandlerException() {
		super();
	}

	public DuplicateChaincodeHandlerException(Handler handler) {
		super();
		this.scID = handler.getSmartContractID();
	}

	public DuplicateChaincodeHandlerException(String message) {
		super(MODULE_NAME + message);
	}

	public DuplicateChaincodeHandlerException(String message, Throwable cause) {
		super(MODULE_NAME + message, cause);
	}


	public DuplicateChaincodeHandlerException(Throwable cause) {
		super(cause);
	}


	protected DuplicateChaincodeHandlerException(String message, Throwable cause,
								boolean enableSuppression,
								boolean writableStackTrace) {
		super(MODULE_NAME + message, cause, enableSuppression, writableStackTrace);
	}
}
