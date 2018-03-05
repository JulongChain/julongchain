/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified by Dingxuan sunianle on 2018-03-01
*/

package org.bcia.javachain.core.smartcontract.shim.fsm.exceptions;

public class NoTransitionException extends Exception {

	public final Exception error;

	public NoTransitionException() {
		this(null);
	}

	public NoTransitionException(Exception error) {
		super("No transition occurred" + (error == null ? "" : " because of error " + error.toString()));
		this.error = error;
	}

}
