/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.javachain.core.smartcontract.fsm;

public class CBDesc {

	public final CallbackType type;
	public final String trigger;
	public final Callback callback;

	public CBDesc(CallbackType type, String trigger, Callback callback) {
		this.type = type;
		this.trigger = trigger;
		this.callback = callback;
	}

}
