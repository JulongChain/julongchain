/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified by Dingxuan sunianle on 2018-03-01
*/

package org.bcia.javachain.core.smartcontract.shim.fsm;

public enum CallbackType {

	NONE,
	BEFORE_EVENT,
	LEAVE_STATE,
	ENTER_STATE,
	AFTER_EVENT;

}
