/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified java_package and other contents by Dingxuan on 2018-08-30
*/

package org.bcia.julongchain.core.smartcontract.shim.fsm;

public class CBDesc {

	public final CallbackType type;
	public final String trigger;
	public final ICallback callback;

	public CBDesc(CallbackType type, String trigger, ICallback callback) {
		this.type = type;
		this.trigger = trigger;
		this.callback = callback;
	}

}
