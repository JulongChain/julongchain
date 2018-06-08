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

import org.bcia.julongchain.common.util.proto.SignedData;

import java.util.List;

/**
 * ICollectionAccessPolicy encapsulates functions for the access policy of a collection
 *
 * @author sunianle, sunzongyu
 * @date 4/27/18
 * @company Dingxuan
 */
public interface ICollectionAccessPolicy {
    /**
     * AccessFilter returns a member filter function for a collection
     */
    boolean getAccessFilter(SignedData sd);

    /**
     * The minimum number of peers private data will be sent to upon
     * endorsement. The endorsement would fail if dissemination to at least
     * this number of peers is not achieved.
     */
    int getRequiredNodeCount();

    /**
     * The maximum number of peers that private data will be sent to
     * upon endorsement. This number has to be bigger than RequiredPeerCount().
     */
    int getMaximumNodeCount();

    /**
     * MemberOrgs returns the collection's members as MSP IDs. This serves as
     * a human-readable way of quickly identifying who is part of a collection.
     */
    List<String> memberOrgs();
}