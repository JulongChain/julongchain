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
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.IQueryExecutor;
import org.bcia.julongchain.protos.common.Collection;

/**
 * a stored backed
 * by a ledger supplied by the specified ledgerGetter with
 * an internal name formed as specified by the supplied
 * collectionNamer function
 *
 * @author sunianle, sunzongyu
 * @date 3/20/18
 * @company Dingxuan
 */
public class SimpleCollectionStore implements ICollectionStore{

    private static final JavaChainLog log = JavaChainLogFactory.getLog(SimpleCollectionStore.class);

    private IPrivDataSupport s;

    public SimpleCollectionStore(IPrivDataSupport s){
        this.s = s;
    }

    @Override
    public ICollection retrieveColletion(Collection.CollectionCriteria collectionCriteria) throws JavaChainException {
        return retrieveSimpleCollection(collectionCriteria);
    }

    @Override
    public ICollectionAccessPolicy retrieveCollectionAccessPolicy(Collection.CollectionCriteria collectionCriteria) throws JavaChainException {
        return retrieveSimpleCollection(collectionCriteria);
    }

    @Override
    public Collection.CollectionConfigPackage retrieveCollectionConfigPackage(Collection.CollectionCriteria cc) throws JavaChainException {
        IQueryExecutor qe = s.getQueryExecotorForLedger(cc.getChannel());
        try {
            byte[] cb = qe.getState("lssc", s.getCollectionKVSKey(cc));
            if(cb == null){
                throw noSuchCollectionError(cc);
            }
            Collection.CollectionConfigPackage collections = null;
            try {
                collections = Collection.CollectionConfigPackage.parseFrom(cb);
            } catch (InvalidProtocolBufferException e) {
                throw new JavaChainException("Invalid configuration for collection criteria " + cc);
            }
            return collections;
        } finally {
            qe.done();
        }
    }

    private SimpleCollection retrieveSimpleCollection(Collection.CollectionCriteria cc) throws JavaChainException{
        Collection.CollectionConfigPackage collections = retrieveCollectionConfigPackage(cc);
        if(collections == null){
            return null;
        }
        for(Collection.CollectionConfig cconf : collections.getConfigList()){
            switch (cconf.getPayloadCase().getNumber()){
                case Collection.CollectionConfig
                        .STATIC_COLLECTION_CONFIG_FIELD_NUMBER:
                    SimpleCollection sc = new SimpleCollection();
                    sc.setUp(cconf.getStaticCollectionConfig(), s.getIdentityDeserializer(cc.getChannel()));
                    return sc;
                default:
                    throw new JavaChainException("Unexpected collection type");
            }
        }
        throw noSuchCollectionError(cc);
    }

    private JavaChainException noSuchCollectionError(Collection.CollectionCriteria cc){
        String errorMsg = String.format("collection %s/%s/%s could not be found", cc.getChannel(), cc.getNamespace(), cc.getCollection());
        log.error(errorMsg);
        return new JavaChainException(errorMsg);
    }
}
