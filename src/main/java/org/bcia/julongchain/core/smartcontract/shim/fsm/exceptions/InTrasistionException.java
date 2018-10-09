/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified java_package and other contents by Dingxuan on 2018-08-30
*/

package org.bcia.julongchain.core.smartcontract.shim.fsm.exceptions;

public class InTrasistionException extends Exception {

	public final String event;

	public InTrasistionException(String event) {
		super("Event '" + event + "' is inappropriate because"
				+ " the previous trasaction had not completed");
		this.event = event;
	}

}
