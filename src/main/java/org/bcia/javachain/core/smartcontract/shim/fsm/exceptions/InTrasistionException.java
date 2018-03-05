/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified by Dingxuan sunianle on 2018-03-01
*/

package org.bcia.javachain.core.smartcontract.shim.fsm.exceptions;

public class InTrasistionException extends Exception {

	public final String event;

	public InTrasistionException(String event) {
		super("Event '" + event + "' is inappropriate because"
				+ " the previous trasaction had not completed");
		this.event = event;
	}

}
