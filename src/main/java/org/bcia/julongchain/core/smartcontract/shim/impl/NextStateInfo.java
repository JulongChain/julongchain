/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified java_package and other contents by Dingxuan on 2018-08-30
*/

package org.bcia.julongchain.core.smartcontract.shim.impl;

import org.bcia.julongchain.protos.node.SmartContractShim;

public class NextStateInfo {

	public SmartContractShim.SmartContractMessage message;
	public boolean sendToSC;

	public NextStateInfo(SmartContractShim.SmartContractMessage message, boolean sendToSC) {
		this.message = message;
		this.sendToSC = sendToSC;
	}

}
