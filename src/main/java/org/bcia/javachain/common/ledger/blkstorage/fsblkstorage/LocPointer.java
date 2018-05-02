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
package org.bcia.javachain.common.ledger.blkstorage.fsblkstorage;

/**
 * 封装读写点
 *
 * @author sunzongyu
 * @date 2018/04/12
 * @company Dingxuan
 */
public class LocPointer {

    private Integer offset;
    private Integer bytesLength;

    public LocPointer(){

    }

    public LocPointer(int offset, int bytesLength){
        this.offset = offset;
        this.bytesLength = bytesLength;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getBytesLength() {
        return bytesLength;
    }

    public void setBytesLength(Integer bytesLength) {
        this.bytesLength = bytesLength;
    }

    @Override
    public String toString() {
        return String.format("offset=%d, byteLength=%d", offset, bytesLength);
    }
}
