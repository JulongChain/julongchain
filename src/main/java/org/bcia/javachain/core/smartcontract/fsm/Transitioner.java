/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.javachain.core.smartcontract.fsm;

import org.hyperledger.fabric.shim.fsm.exceptions.NotInTransitionException;

public class Transitioner {

	public void transition(FSM fsm) throws NotInTransitionException {
		if (fsm.transition == null) {
			throw new NotInTransitionException();
		}
		fsm.transition.run();
		fsm.transition = null;
	}

}
