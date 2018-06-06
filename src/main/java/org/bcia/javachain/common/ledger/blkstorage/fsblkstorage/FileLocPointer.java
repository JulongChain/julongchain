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

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.core.ledger.util.Util;

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

    public static FileLocPointer newFileLocationPointer(Integer fileSuffixNum, long beginningOffset, LocPointer relativeLP) {
        FileLocPointer flp = new FileLocPointer();
        LocPointer lp = new LocPointer();
        lp.setOffset(relativeLP.getOffset());
        lp.setBytesLength(relativeLP.getBytesLength());
        flp.setFileSuffixNum(fileSuffixNum);
        flp.setLocPointer(lp);
        return flp;
    }

    byte[] marshal() {
        byte[] fileSUffixNumBytes = Util.longToBytes(fileSuffixNum, 8);
        byte[] offsetBytes = Util.longToBytes(locPointer.getOffset(), 8);
        byte[] bytesLengthBytes = Util.longToBytes(locPointer.getBytesLength(), 8);
        byte[] result = ArrayUtils.addAll(fileSUffixNumBytes, offsetBytes);
        return ArrayUtils.addAll(result, bytesLengthBytes);
    }

    void unmarshal(byte[] b) {
        fileSuffixNum = ((int) Util.bytesToLong(b, 0, 8));
        if(locPointer == null){
            locPointer = new LocPointer();
        }
        locPointer.setOffset(((int) Util.bytesToLong(b, 8, 8)));
        locPointer.setBytesLength(((int) Util.bytesToLong(b, 16, 8)));
    }

    public Integer getFileSuffixNum() {
        return fileSuffixNum;
    }

    public void setFileSuffixNum(Integer fileSuffixNum) {
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
