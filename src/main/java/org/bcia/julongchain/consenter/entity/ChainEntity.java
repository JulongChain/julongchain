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
package org.bcia.julongchain.consenter.entity;

import org.bcia.julongchain.protos.common.Common;

/**
 * 类描述
 *
 * @author
 * @date 2018/5/2
 * @company Shudun
 */

public class ChainEntity {
    private long lastOriginalOffsetProcessed;
    private String chainID;
    private int lastCutBlockNumber;
    private String timer;


    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public int getLastCutBlockNumber() {
        return lastCutBlockNumber;
    }

    public void setLastCutBlockNumber(int lastCutBlockNumber) {
        this.lastCutBlockNumber = lastCutBlockNumber;
    }

    public String getChainID() {
        return chainID;
    }

    public void setChainID(String chainID) {
        this.chainID = chainID;
    }

    public long getLastOriginalOffsetProcessed() {
        return lastOriginalOffsetProcessed;
    }

    public void setLastOriginalOffsetProcessed(long lastOriginalOffsetProcessed) {
        this.lastOriginalOffsetProcessed = lastOriginalOffsetProcessed;
    }



    public Long processNormalMsg( Common.Envelope env){
        int a = 0;
        long b = (int)a;
            return  b;
    }
    public int sequence(){
        return 0;
    }
}
