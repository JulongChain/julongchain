/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified by Dingxuan sunianle on 2018-03-01
*/

package org.bcia.javachain.core.smartcontract.shim.fsm.exceptions;

public class UnknownEventException extends Exception {

	public final String event;

	public UnknownEventException(String event) {
		super("Event '" + event + "' does not exist");
		this.event = event;
	}

}
