/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified by Dingxuan sunianle on 2018-03-01
*/

package org.bcia.javachain.core.smartcontract.shim.impl;

import org.bcia.javachain.core.ledger.IQueryResultsIterator;
import org.bcia.javachain.protos.node.SmartcontractShim;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

class QueryResultsIteratorImpl<T> implements IQueryResultsIterator<T> {

	private final Handler handler;
	private final String channelId;
	private final String txId;
	private Iterator<SmartcontractShim.QueryResultBytes> currentIterator;
	private SmartcontractShim.QueryResponse currentQueryResponse;
	private Function<SmartcontractShim.QueryResultBytes, T> mapper;

	public QueryResultsIteratorImpl(final Handler handler, final String channelId, final String txId, final SmartcontractShim.QueryResponse queryResponse, Function<SmartcontractShim.QueryResultBytes, T> mapper) {
		this.handler = handler;
		this.channelId = channelId;
		this.txId = txId;
		this.currentQueryResponse = queryResponse;
		this.currentIterator = currentQueryResponse.getResultsList().iterator();
		this.mapper = mapper;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return currentIterator.hasNext() || currentQueryResponse.getHasMore();
			}

			@Override
			public T next() {

				// return next fetched result, if any
				if(currentIterator.hasNext()) return mapper.apply(currentIterator.next());

				// throw exception if there are no more expected results
				if(!currentQueryResponse.getHasMore()) throw new NoSuchElementException();

				// get more results from peer
				currentQueryResponse = handler.queryStateNext(channelId, txId, currentQueryResponse.getId());
				currentIterator = currentQueryResponse.getResultsList().iterator();

				// return next fetched result
				return mapper.apply(currentIterator.next());

			}

		};
	}

	@Override
	public void close() throws Exception {
		this.handler.queryStateClose(channelId, txId, currentQueryResponse.getId());
		this.currentIterator = Collections.emptyIterator();
		this.currentQueryResponse = SmartcontractShim.QueryResponse.newBuilder().setHasMore(false).build();
	}

}
