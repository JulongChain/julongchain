/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.julongchain.core.smartcontract.shim.fsm.exceptions;

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
