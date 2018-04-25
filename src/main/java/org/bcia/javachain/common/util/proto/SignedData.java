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
package org.bcia.javachain.common.util.proto;

/**
 * SignedData is used to represent the general triplet required to verify a signature
* This is intended to be generic across crypto schemes, while most crypto schemes will
* include the signing identity and a nonce within the Data, this is left to the crypto
* implementation
 *
 * @author sunianle
 * @date 3/21/18
 * @company Dingxuan
 */
public class SignedData {
    private byte[] data;
    private byte[] identity;
    private byte[] signature;

    public SignedData(byte[] data, byte[] identity, byte[] signature) {
        this.data = data;
        this.identity = identity;
        this.signature = signature;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getIdentity() {
        return identity;
    }

    public byte[] getSignature() {
        return signature;
    }


}
