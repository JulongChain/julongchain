/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified java_package and other contents by Dingxuan on 2018-08-30
*/

package org.bcia.julongchain.core.smartcontract.shim.fsm;

/**
 * Represents an event when initializing the FSM.
 * The event can have one or more source states that is valid for performing
 * the transition. If the FSM is in one of the source states it will end up in
 * the specified destination state, calling all defined callbacks as it goes.
 */
public class EventDesc {

		/** The event name used when calling for a transition */
		String name;

		/** A slice of source states that the FSM must be in to perform a state transition */
		String[] src;

		/** The destination state that the FSM will be in if the transition succeeds */
		String dst;

		public EventDesc(String name, String[] src, String dst) {
			this.name = name;
			this.src = src;
			this.dst = dst;
		}
		public EventDesc(String name, String src, String dst) {
			this.name = name;
			this.src = new String[] { src };
			this.dst = dst;
		}
}
