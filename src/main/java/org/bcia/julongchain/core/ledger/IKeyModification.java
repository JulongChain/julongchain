/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
Modified by Dingxuan sunianle on 2018-03-01
*/

package org.bcia.julongchain.core.ledger;

public interface IKeyModification {

	/**
	 * Returns the transaction id.
	 *
	 * @return
	 */
	String getTxId();

	/**
	 * Returns the key's value at the time returned by {@link #getTimestamp()}.
	 *
	 * @return
	 */
	byte[] getValue();

	/**
	 * Returns the key's value at the time returned by {@link #getTimestamp()},
	 * decoded as a UTF-8 string.
	 *
	 * @return
	 */
	String getStringValue();

	/**
	 * Returns the timestamp of the key modification entry.
	 *
	 * @return
	 */
	java.time.Instant getTimestamp();

	/**
	 * Returns the deletion marker.
	 *
	 * @return
	 */
	boolean isDeleted();

}