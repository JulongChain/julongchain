/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified by Dingxuan sunianle on 2018-03-01
*/

package org.bcia.javachain.core.smartcontract;


import org.bcia.javachain.protos.node.SmartcontractShim;

public class NextStateInfo {

	public SmartcontractShim.SmartContractMessage message;
	public boolean sendToCC;

	public NextStateInfo(SmartcontractShim.SmartContractMessage message, boolean sendToCC) {
		this.message = message;
		this.sendToCC = sendToCC;
	}

}
