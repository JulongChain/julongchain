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

/**
 * In-process VM异常类
 *
 * @author sunzongyu
 * @date 2018/07/23
 * @company Dingxuan
 */
public class InprocVMException extends VMException {
	private static final String MODULE_NAME = "[InprocVM]";

	public InprocVMException() {
		super();
	}

	public InprocVMException(String message) {
		super(MODULE_NAME + message);
	}

	public InprocVMException(String message, Throwable cause) {
		super(MODULE_NAME + message, cause);
	}


	public InprocVMException(Throwable cause) {
		super(cause);
	}


	protected InprocVMException(String message, Throwable cause,
							  boolean enableSuppression,
							  boolean writableStackTrace) {
		super(MODULE_NAME + message, cause, enableSuppression, writableStackTrace);
	}
}
