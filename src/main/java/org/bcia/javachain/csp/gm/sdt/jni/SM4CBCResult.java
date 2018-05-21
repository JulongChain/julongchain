/**
 * Copyright SDT. All Rights Reserved.
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
package org.bcia.javachain.csp.gm.sdt.jni;

/**
 * SM4 CBC模式加密结果
 *
 * @author tengxiumin
 * @date 2018/05/15
 * @company SDT
 */
public class SM4CBCResult {

    private byte[] iv;
    private byte[] data;

    public SM4CBCResult(byte[] iv, byte[] data) {
        this.iv = iv;
        this.data = data;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getData() {
        return data;
    }
}
