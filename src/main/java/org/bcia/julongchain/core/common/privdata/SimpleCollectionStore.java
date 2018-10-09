/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.common.privdata;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.ledger.IQueryExecutor;
import org.bcia.julongchain.protos.common.Collection;

/**
 * 简单集合仓库
 * @author sunianle, sunzongyu
 * @date 3/20/18
 * @company Dingxuan
 */
public class SimpleCollectionStore implements ICollectionStore{

    private static JulongChainLog log = JulongChainLogFactory.getLog(SimpleCollectionStore.class);

    private IPrivDataSupport s;

    public SimpleCollectionStore(IPrivDataSupport s){
        this.s = s;
    }

    @Override
    public ICollection retrieveColletion(Collection.CollectionCriteria collectionCriteria) throws JulongChainException {
        return retrieveSimpleCollection(collectionCriteria);
    }

    @Override
    public ICollectionAccessPolicy retrieveCollectionAccessPolicy(Collection.CollectionCriteria collectionCriteria) throws JulongChainException {
        return retrieveSimpleCollection(collectionCriteria);
    }

    @Override
    public Collection.CollectionConfigPackage retrieveCollectionConfigPackage(Collection.CollectionCriteria cc) throws JulongChainException {
        IQueryExecutor qe = s.getQueryExecutorForLedger(cc.getGroup());
        try {
            byte[] cb = qe.getState("lssc", s.getCollectionKVSKey(cc));
            if(cb == null){
                throw noSuchCollectionError(cc);
            }
            Collection.CollectionConfigPackage collections = null;
            try {
                collections = Collection.CollectionConfigPackage.parseFrom(cb);
            } catch (InvalidProtocolBufferException e) {
                throw new JulongChainException("Invalid configuration for collection criteria " + cc);
            }
            return collections;
        } finally {
            qe.done();
        }
    }

    private SimpleCollection retrieveSimpleCollection(Collection.CollectionCriteria cc) throws JulongChainException {
        Collection.CollectionConfigPackage collections = retrieveCollectionConfigPackage(cc);
        if(collections == null){
            return null;
        }
        for(Collection.CollectionConfig cconf : collections.getConfigList()){
            switch (cconf.getPayloadCase().getNumber()){
                case Collection.CollectionConfig
                        .STATIC_COLLECTION_CONFIG_FIELD_NUMBER:
                    SimpleCollection sc = new SimpleCollection();
                    sc.setUp(cconf.getStaticCollectionConfig(), s.getIdentityDeserializer(cc.getGroup()));
                    return sc;
                default:
                    throw new JulongChainException("Unexpected collection type");
            }
        }
        throw noSuchCollectionError(cc);
    }

    private JulongChainException noSuchCollectionError(Collection.CollectionCriteria cc){
        String errorMsg = String.format("collection %s/%s/%s could not be found", cc.getGroup(), cc.getNamespace(), cc.getCollection());
        log.error(errorMsg);
        return new JulongChainException(errorMsg);
    }
}
