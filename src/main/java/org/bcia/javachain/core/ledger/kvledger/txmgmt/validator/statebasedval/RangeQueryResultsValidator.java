/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.statebasedval;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.List;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public class RangeQueryResultsValidator implements RangeQueryValidator {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(RangeQueryResultsValidator.class);

    private KvRwset.RangeQueryInfo rqInfo;
    private ResultsIterator itr;

    @Override
    public void init(KvRwset.RangeQueryInfo rqInfo, ResultsIterator itr) {
        this.rqInfo = rqInfo;
        this.itr = itr;
    }

    @Override
    public boolean validate() throws LedgerException {
        List<KvRwset.KVRead> rqResults = rqInfo.getRawReads().getKvReadsList();
        QueryResult result = itr.next();
        if(rqResults == null){
            return result == null;
        }
        for(KvRwset.KVRead kvRead : rqResults){
            logger.debug("Comparing kvRead to queryResponse");
            if(result == null){
                logger.debug("Query response null.Key " + kvRead.getKey() + " got deleted");
                return false;
            }
            if(!result.getCompositeKey().getKey().equals(kvRead.getKey())){
                logger.debug(String.format("Key name mismatch: key in rwset = %s, key in query result = %s", kvRead.getKey(), result.getCompositeKey().getKey()));
                return false;
            }
            if(!Height.areSame(result.getVersionedValue().getVersion(), convertToVersionHeight(kvRead.getVersion()))){
                logger.debug(String.format("Version mismatch: key = %s", kvRead.getKey()));
                return false;
            }
            itr.next();
        }
        if(result != null){
            logger.debug("Extra result = ", result);
            return false;
        }
        return true;
    }

    private Height convertToVersionHeight(KvRwset.Version v){
        return Height.newHeight(v.getBlockNum(), v.getTxNum());
    }
}
