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

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.protos.common.Collection;

/**
 * ICollectionStore retrieves stored collections based on the collection's
 * properties. It works as a collection object factory and takes care of
 * returning a collection object of an appropriate collection type.
 *
 * @author sunianle
 * @date 3/15/18
 * @company Dingxuan
 */
public interface ICollectionStore {
    /**
     *  GetCollection retrieves the collection in the following way:
     *  If the TxID exists in the ledger, the collection that is returned has the
     *  latest configuration that was committed into the ledger before this txID
     *  was committed.
     *  Else - it's the latest configuration for the collection.
     * @param collectionCriteria
     * @return
     * @throws JavaChainException
     */
    ICollection retrieveColletion(Collection.CollectionCriteria collectionCriteria)throws JavaChainException;

    /**
     * GetCollectionAccessPolicy retrieves a collection's access policy
     * @param collectionCriteria
     * @return
     * @throws JavaChainException
     */
    ICollectionAccessPolicy retrieveCollectionAccessPolicy(Collection.CollectionCriteria collectionCriteria)throws JavaChainException;

    /**
     * RetrieveCollectionConfigPackage retrieves the configuration
     * for the collection with the supplied criteria
     * @param collectionCriteria
     * @return
     * @throws JavaChainException
     */
    Collection.CollectionConfigPackage retrieveCollectionConfigPackage(Collection.CollectionCriteria collectionCriteria)throws JavaChainException;

}
