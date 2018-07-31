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
package org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.core.ledger.util.Util;

/**
 * 封装读取的区块文件位置
 *
 * @author sunzongyu
 * @date 2018/04/12
 * @company Dingxuan
 */
public class FileLocPointer {

    private int fileSuffixNum;
    private LocPointer locPointer;

    /**
     * 默认构造方法
     */
    public FileLocPointer(){}

    public FileLocPointer(int fileSuffixNum, LocPointer relativeLP){
        this.fileSuffixNum = fileSuffixNum;
        this.locPointer = relativeLP;
    }

    public FileLocPointer(int fileSuffixNum, long offset, long bytesLength){
        this.fileSuffixNum = fileSuffixNum;
        this.locPointer = new LocPointer(offset, bytesLength);
    }

    public byte[] marshal() {
        byte[] fileSUffixNumBytes = Util.longToBytes(fileSuffixNum, BlockFileManager.PEEK_BYTES_LEN);
        byte[] offsetBytes = Util.longToBytes(locPointer.getOffset(), BlockFileManager.PEEK_BYTES_LEN);
        byte[] bytesLengthBytes = Util.longToBytes(locPointer.getBytesLength(), BlockFileManager.PEEK_BYTES_LEN);
        byte[] result = ArrayUtils.addAll(fileSUffixNumBytes, offsetBytes);
        return ArrayUtils.addAll(result, bytesLengthBytes);
    }

    public void unmarshal(byte[] b) {
        fileSuffixNum = ((int) Util.bytesToLong(b, 0, BlockFileManager.PEEK_BYTES_LEN));
        if(locPointer == null){
            locPointer = new LocPointer();
        }
        locPointer.setOffset(((int) Util.bytesToLong(b, 8, BlockFileManager.PEEK_BYTES_LEN)));
        locPointer.setBytesLength(((int) Util.bytesToLong(b, 16, BlockFileManager.PEEK_BYTES_LEN)));
    }

    public int getFileSuffixNum() {
        return fileSuffixNum;
    }

    public void setFileSuffixNum(int fileSuffixNum) {
        this.fileSuffixNum = fileSuffixNum;
    }

    public LocPointer getLocPointer() {
        return locPointer;
    }

    public void setLocPointer(LocPointer locPointer) {
        this.locPointer = locPointer;
    }

    @Override
    public String toString() {
        return String.format("fielSuffixNum=%d, %s", fileSuffixNum, locPointer);
    }
}
