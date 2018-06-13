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
package org.bcia.julongchain.core.commiter;


import org.bcia.julongchain.protos.common.Common;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/23
 * @company Dingxuan
 */
public class BlockValidationRequest {
    private Common.Block block;
    private byte[] data;
    private int txIndex;
    private ICommitterValidator committerValidator;

    public BlockValidationRequest(Common.Block block, byte[] data, int txIndex, ICommitterValidator committerValidator) {
        this.block = block;
        this.data = data;
        this.txIndex = txIndex;
        this.committerValidator = committerValidator;
    }

    public Common.Block getBlock() {
        return block;
    }

    public byte[] getData() {
        return data;
    }

    public int getTxIndex() {
        return txIndex;
    }

    public ICommitterValidator getCommitterValidator() {
        return committerValidator;
    }
}
