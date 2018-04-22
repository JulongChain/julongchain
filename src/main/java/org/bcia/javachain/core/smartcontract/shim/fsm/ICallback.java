/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.javachain.core.smartcontract.shim.fsm;

public interface ICallback {

	public void run(Event event);

}
