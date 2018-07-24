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
package org.bcia.julongchain.gossip.gossip;

public abstract class IFilter {

    private byte[] bytes1 = new byte[]{};

    public abstract Boolean filter(byte[] bytes2);

    public byte[] getBytes1() {
        return bytes1;
    }

    public void setBytes1(byte[] bytes1) {
        this.bytes1 = bytes1;
    }
}
