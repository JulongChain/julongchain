/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified java_package and other contents by Dingxuan on 2018-08-30
*/

package org.bcia.julongchain.core.smartcontract.shim.fsm.exceptions;

public class AsyncException extends Exception {

	public final Exception error;

	public AsyncException() {
		this(null);
	}

	public AsyncException(Exception error) {
		super("Async started" + error == null ?
				"" : " with error " + error.toString());
		this.error = error;
	}

}
