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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate;

/**
 * 更新包总和
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class UpdateBatch {
    private PubUpdateBatch pubUpdateBatch;
    private HashedUpdateBatch hashUpdates;
    private PvtUpdateBatch pvtUpdateBatch;

    public UpdateBatch(){
        this.hashUpdates = new HashedUpdateBatch();
        this.pubUpdateBatch = new PubUpdateBatch();
        this.pvtUpdateBatch = new PvtUpdateBatch();
    }

    public UpdateBatch(PubUpdateBatch pubUpdateBatch, HashedUpdateBatch hashUpdates, PvtUpdateBatch pvtUpdateBatch){
        this.pubUpdateBatch = pubUpdateBatch;
        this.hashUpdates = hashUpdates;
        this.pvtUpdateBatch = pvtUpdateBatch;
    }

    public PubUpdateBatch getPubUpdateBatch() {
        return pubUpdateBatch;
    }

    public void setPubUpdateBatch(PubUpdateBatch pubUpdateBatch) {
        this.pubUpdateBatch = pubUpdateBatch;
    }

    public HashedUpdateBatch getHashUpdates() {
        return hashUpdates;
    }

    public void setHashUpdates(HashedUpdateBatch hashUpdates) {
        this.hashUpdates = hashUpdates;
    }

    public PvtUpdateBatch getPvtUpdateBatch() {
        return pvtUpdateBatch;
    }

    public void setPvtUpdateBatch(PvtUpdateBatch pvtUpdateBatch) {
        this.pvtUpdateBatch = pvtUpdateBatch;
    }
}
