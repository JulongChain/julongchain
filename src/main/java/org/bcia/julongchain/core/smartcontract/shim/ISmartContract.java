/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.julongchain.core.smartcontract.shim;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Defines methods that all smartcontracts must implement.
 */
public interface ISmartContract {
	/**
	 * Called during an instantiate transaction after the container has been
	 * established, allowing the smartcontract to initialize its internal data.
	 */
	public SmartContractResponse init(ISmartContractStub stub);

	/**
	 * Called for every Invoke transaction. The smartcontract may change its state
	 * variables.
	 */
	public SmartContractResponse invoke(ISmartContractStub stub);

	/**
	 * 获取智能合约ＩＤ
	 * @return  智能合约ＩＤ
	 */
	String getSmartContractID();

	/**
	 * 获取智能合约的文字描述
	 * @return  智能合约的描述
	 */
	String getSmartContractStrDescription();


	public static class SmartContractResponse {

		private final Status status;
		private final String message;
		private final byte[] payload;

		public SmartContractResponse(Status status, String message, byte[] payload) {
			this.status = status;
			this.message = message;
			this.payload = payload;
		}

		public Status getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}

		public byte[] getPayload() {
			return payload;
		}

		public String getStringPayload() {
			return new String(payload, UTF_8);
		}

		public enum Status {
			SUCCESS(200),
			INTERNAL_SERVER_ERROR(500),
			ERRORTHRESHOLD(600);

			private static final Map<Integer, Status> codeToStatus = new HashMap<>();
			private final int code;

			private Status(int code) {
				this.code = code;
			}

			public int getCode() {
				return code;
			}

			public static Status forCode(int code) {
				final Status result = codeToStatus.get(code);
				if(result == null) {
					throw new IllegalArgumentException("no status for code " + code);
				}
				return result;
			}

			static {
				for (Status status : Status.values()) {
					codeToStatus.put(status.code, status);
				}
			}

		}

	}
}
