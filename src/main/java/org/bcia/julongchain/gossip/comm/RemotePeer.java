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
package org.bcia.julongchain.gossip.comm;

/**
 * class description
 *
 * @author wanliangbing
 * @date 18-7-24
 * @company Dingxuan
 */
public class RemotePeer {

    public String endpoint;
    public byte[] PKIID;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public byte[] getPKIID() {
        return PKIID;
    }

    public void setPKIID(byte[] PKIID) {
        this.PKIID = PKIID;
    }
}
