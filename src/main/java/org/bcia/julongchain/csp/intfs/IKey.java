package org.bcia.julongchain.csp.intfs;

/**
 * Copyright BCIA. All Rights Reserved.
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

/**
 * @author zhanglin
 * @purpose Define the interface, IKey
 * @date 2018-01-25
 * @company Dingxuan
 */

// IKey represents a cryptographic key, either symmetric one or asymmetric one.
public interface IKey {

    // The toBytes converts this key to its byte representation.
    byte[] toBytes();

    // The ski returns the subject key identifier of this key.
    byte[] ski();

    // The isSymmetric returns true if this key is a symmetric key, false otherwise.
    boolean isSymmetric();

    // The isPrivate returns true if this key is a private key, false otherwise.
    boolean isPrivate();

    // The getPublicKey returns the corresponding public key part of an asymmetric public/private key pair.
    // This method returns null in symmetric key schemes.
    IKey getPublicKey();

}
