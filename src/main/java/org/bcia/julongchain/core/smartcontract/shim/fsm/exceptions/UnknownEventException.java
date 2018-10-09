/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified java_package and other contents by Dingxuan on 2018-08-30
*/

package org.bcia.julongchain.core.smartcontract.shim.fsm.exceptions;

public class UnknownEventException extends Exception {

	public final String event;

	public UnknownEventException(String event) {
		super("Event '" + event + "' does not exist");
		this.event = event;
	}

}
