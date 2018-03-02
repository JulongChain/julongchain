/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.javachain.core.smartcontract.fsm;

public enum CallbackType {

	NONE,
	BEFORE_EVENT,
	LEAVE_STATE,
	ENTER_STATE,
	AFTER_EVENT;

}
