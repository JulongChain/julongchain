/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.msp.entity;

/**
 * @author zhangmingyang
 * @Date: 2018/3/6
 * @company Dingxuan
 */
public class IdentityIdentifier {
    public String Mspid;
    public String Id;

    public IdentityIdentifier(String mspid, String id) {
        Mspid = mspid;
        Id = id;
    }

    public IdentityIdentifier(String mspid) {
        Mspid = mspid;
    }

    public String getMspid() {
        return Mspid;
    }

    public void setMspid(String mspid) {
        Mspid = mspid;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
