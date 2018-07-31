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
 * 私有数据更新包
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class PvtUpdateBatch {
    private UpdateMap map;

    public PvtUpdateBatch() {
        this.map = new UpdateMap();
    }

    public UpdateMap getMap() {
        return map;
    }

    public void setMap(UpdateMap map) {
        this.map = map;
    }
}
