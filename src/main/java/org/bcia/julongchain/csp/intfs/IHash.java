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
 * @purpose Define the interface, IHash
 * @date 2018-01-25
 * @company Dingxuan
 */

// IHash is the common interface implemented by all hash functions.
public interface IHash {

    // The write method writes len(p) bytes from p to the underlying data stream.
    // It returns the number of bytes written from p (that is n, and 0 <= n <= len(p)).
    // The write must not modify the slice data, even temporarily.
    //
    // Implementations must not retain p.
    int write(byte[] p);

    // The sum appends the current hash to b and returns the resulting slice.
    // It does not change the underlying hash state.
    byte[] sum(byte[] b);

    // The reset method resets the Hash to its initial state.
    void reset();

    // The size method returns the number of bytes sum will return.
    int size();

    // The blockSize method returns the hash's underlying block size.
    // The write method must be able to accept any amount of data, but it may operate more
    // efficiently if all writes are a multiple of the block size.
    int blockSize();

}
