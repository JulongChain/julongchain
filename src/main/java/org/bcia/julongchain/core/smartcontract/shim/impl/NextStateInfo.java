/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.julongchain.core.smartcontract.shim.impl;

import org.bcia.julongchain.protos.node.SmartcontractShim;

public class NextStateInfo {

	public SmartcontractShim.SmartContractMessage message;
	public boolean sendToSC;

	public NextStateInfo(SmartcontractShim.SmartContractMessage message, boolean sendToSC) {
		this.message = message;
		this.sendToSC = sendToSC;
	}

}
