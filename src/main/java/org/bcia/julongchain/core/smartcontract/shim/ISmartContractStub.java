/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.julongchain.core.smartcontract.shim;

import org.bcia.julongchain.core.smartcontract.shim.ledger.CompositeKey;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IKeyModification;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IKeyValue;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IQueryResultsIterator;
import org.bcia.julongchain.protos.node.ProposalPackage.SignedProposal;
import org.bcia.julongchain.protos.node.SmartContractEventPackage;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public interface ISmartContractStub {

	/**
	 * Returns the arguments corresponding to the call to
	 * {@link ISmartContract#init(ISmartContractStub)} or
	 * {@link ISmartContract#invoke(ISmartContractStub)}.
	 *
	 * @return a list of arguments
	 */
	List<byte[]> getArgs();

	/**
	 * Returns the arguments corresponding to the call to
	 * {@link ISmartContract#init(ISmartContractStub)} or
	 * {@link ISmartContract#invoke(ISmartContractStub)}.
	 *
	 * @return a list of arguments cast to UTF-8 strings
	 */
	List<String> getStringArgs();

	/**
	 * A convenience method that returns the first argument of the smartContract
	 * invocation for use as a function name.
	 *
	 * The bytes of the first argument are decoded as a UTF-8 string.
	 *
	 * @return the function name
	 */
	String getFunction();

	/**
	 * A convenience method that returns all except the first argument of the
	 * smartContract invocation for use as the parameters to the function returned
	 * by #{@link ISmartContractStub#getFunction()}.
	 *
	 * The bytes of the arguments are decoded as a UTF-8 strings and returned as
	 * a list of string parameters..
	 *
	 * @return a list of parameters
	 */
	List<String> getParameters();

	/**
	 * Returns the transaction id
	 *
	 * @return the transaction id
	 */
	String getTxId();

	/**
	 * Returns the channel id
	 *
	 * @return the channel id
	 */
	String getGroupId();

	/**
	 * Invoke another smartContract using the same transaction context.
	 *
	 * @param smartContractName
	 *            Name of smartContract to be invoked.
	 * @param args
	 *            Arguments to pass on to the called smartContract.
	 * @param channel
	 *            If not specified, the caller's channel is assumed.
	 * @return
	 */
	ISmartContract.SmartContractResponse invokeSmartContract(String smartContractName, List<byte[]> args, String
            channel);

	/**
	 * Returns the byte array value specified by the key, from the ledger.
	 *
	 * @param key
	 *            name of the value
	 * @return value the value read from the ledger
	 */
	byte[] getState(String key);

	/**
	 * Writes the specified value and key into the ledger
	 *
	 * @param key
	 *            name of the value
	 * @param value
	 *            the value to write to the ledger
	 */
	void putState(String key, byte[] value);

	/**
	 * Removes the specified key from the ledger
	 *
	 * @param key
	 *            name of the value to be deleted
	 */
	void delState(String key);

	/**
	 * Returns all existing keys, and their values, that are lexicographically
	 * between <code>startkey</code> (inclusive) and the <code>endKey</code>
	 * (exclusive).
	 *
	 * @param startKey
	 * @param endKey
	 * @return an {@link Iterable} of {@link IKeyValue}
	 */
	IQueryResultsIterator<IKeyValue> getStateByRange(String startKey, String endKey);

	/**
	 * Returns all existing keys, and their values, that are prefixed by the
	 * specified partial {@link CompositeKey}.
	 *
	 * If a full composite key is specified, it will not match itself, resulting
	 * in no keys being returned.
	 *
	 * @param compositeKey
	 *            partial composite key
	 * @return an {@link Iterable} of {@link IKeyValue}
	 */
	IQueryResultsIterator<IKeyValue> getStateByPartialCompositeKey(String compositeKey);

	/**
	 * Given a set of attributes, this method combines these attributes to
	 * return a composite key.
	 *
	 * @param objectType
	 * @param attributes
	 * @return a composite key
	 */
	CompositeKey createCompositeKey(String objectType, String... attributes);

	/**
	 * Parses a composite key from a string.
	 *
	 * @param compositeKey
	 *            a composite key string
	 * @return a composite key
	 */
	CompositeKey splitCompositeKey(String compositeKey);

	/**
	 * Perform a rich query against the state database.
	 *
	 * @param query
	 *            query string in a syntax supported by the underlying state
	 *            database
	 * @return
	 * @throws UnsupportedOperationException
	 *             if the underlying state database does not support rich
	 *             queries.
	 */
	IQueryResultsIterator<IKeyValue> getQueryResult(String query);

	/**
	 * Returns the history of the specified key's values across time.
	 *
	 * @param key
	 * @return an {@link Iterable} of {@link IKeyModification}
	 */
	IQueryResultsIterator<IKeyModification> getHistoryForKey(String key);

	/**
	 * Defines the SMARTCONTRACT type event that will be posted to interested
	 * clients when the smartContract's result is committed to the ledger.
	 *
	 * @param name
	 *            Name of event. Cannot be null or empty string.
	 * @param payload
	 *            Optional event payload.
	 */
	void setEvent(String name, byte[] payload);

	/**
	 * Invoke another smartContract using the same transaction context.
	 *
	 * @param smartContractName
	 *            Name of smartContract to be invoked.
	 * @param args
	 *            Arguments to pass on to the called smartContract.
	 * @return
	 */
	default ISmartContract.SmartContractResponse invokeSmartContract(String smartContractName, List<byte[]> args) {
		return invokeSmartContract(smartContractName, args, null);
	}

	/**
	 * Invoke another smartContract using the same transaction context.
	 *
	 * This is a convenience version of
	 * {@link #invokeSmartContract(String, List, String)}. The string args will be
	 * encoded into as UTF-8 bytes.
	 *
	 * @param smartContractName
	 *            Name of smartContract to be invoked.
	 * @param args
	 *            Arguments to pass on to the called smartContract.
	 * @param channel
	 *            If not specified, the caller's channel is assumed.
	 * @return
	 */
	default ISmartContract.SmartContractResponse invokeSmartContractWithStringArgs(String smartContractName,
                                                                                   List<String> args, String channel) {
		return invokeSmartContract(smartContractName, args.stream().map(x -> x.getBytes(UTF_8)).collect(toList()), channel);
	}

	/**
	 * Invoke another smartContract using the same transaction context.
	 *
	 * This is a convenience version of {@link #invokeSmartContract(String, List)}.
	 * The string args will be encoded into as UTF-8 bytes.
	 *
	 *
	 * @param smartContractName
	 *            Name of smartContract to be invoked.
	 * @param args
	 *            Arguments to pass on to the called smartContract.
	 * @return
	 */
	default ISmartContract.SmartContractResponse invokeSmartContractWithStringArgs(String smartContractName, List<String> args) {
		return invokeSmartContractWithStringArgs(smartContractName, args, null);
	}

	/**
	 * Invoke another smartContract using the same transaction context.
	 *
	 * This is a convenience version of {@link #invokeSmartContract(String, List)}.
	 * The string args will be encoded into as UTF-8 bytes.
	 *
	 *
	 * @param smartContractName
	 *            Name of smartContract to be invoked.
	 * @param args
	 *            Arguments to pass on to the called smartContract.
	 * @return
	 */
	default ISmartContract.SmartContractResponse invokeSmartContractWithStringArgs(final String smartContractName, final String... args) {
		return invokeSmartContractWithStringArgs(smartContractName, Arrays.asList(args), null);
	}

	/**
	 * Returns the byte array value specified by the key and decoded as a UTF-8
	 * encoded string, from the ledger.
	 *
	 * @param key
	 *            name of the value
	 * @return value the value read from the ledger
	 */
	default String getStringState(String key) {
		return new String(getState(key), UTF_8);
	}

	/**
	 * Writes the specified value and key into the ledger
	 *
	 * @param key
	 *            name of the value
	 * @param value
	 *            the value to write to the ledger
	 */
	default void putStringState(String key, String value) {
		putState(key, value.getBytes(UTF_8));
	}

	/**
	 * Returns the SMARTCONTRACT type event that will be posted to interested
	 * clients when the smartContract's result is committed to the ledger.
	 *
	 * @return the smartContract event or null
	 */
	SmartContractEventPackage.SmartContractEvent getEvent();

	/**
	 * Returns the signed transaction proposal currently being executed.
	 *
	 * @return null if the current transaction is an internal call to a system
	 *         smartContract.
	 */
	SignedProposal getSignedProposal();

	/**
	 * Returns the timestamp when the transaction was created.
	 *
	 * @return timestamp as specified in the transaction's channel header.
	 */
	Instant getTxTimestamp();

	/**
	 * Returns the identity of the agent (or user) submitting the transaction.
	 *
	 * @return the bytes of the creator field of the proposal's signature
	 *         header.
	 */
	byte[] getCreator();

	/**
	 * Returns the transient map associated with the current transaction.
	 *
	 * @return
	 */
	Map<String, byte[]> getTransient();

	/**
	 * Returns the transaction binding.
	 *
	 */
	byte[] getBinding();

}
