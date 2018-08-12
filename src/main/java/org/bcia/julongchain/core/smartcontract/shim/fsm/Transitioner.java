/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.julongchain.core.smartcontract.shim.fsm;

import org.bcia.julongchain.core.smartcontract.shim.fsm.exceptions.NotInTransitionException;

public class Transitioner {

	public void transition(FSM fsm) throws NotInTransitionException {
		if (fsm.transition == null) {
			throw new NotInTransitionException();
		}
		fsm.transition.run();
		fsm.transition = null;
	}

}
