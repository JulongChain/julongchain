/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.julongchain.core.smartcontract.shim.fsm.exceptions;

public class NotInTransitionException extends Exception {

	public NotInTransitionException() {
		super("The transition is inappropriate"
				+ " because there is no state change in progress");
	}

}
