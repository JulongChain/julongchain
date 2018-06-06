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

import org.bcia.javachain.common.exception.LedgerException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * 读取文件
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockFileReader {

    private File file;

    public static BlockFileReader newBlockfileReader(String filePath){
        BlockFileReader reader = new BlockFileReader();
        reader.file = new File(filePath);
        return reader;
    }

    /**
     * 从offset位起,读取length字节
     */
    public byte[] read(long offset, long length) throws LedgerException {
        FileInputStream fis;
        ByteArrayOutputStream baos;
        byte[] buff = new byte[1024];
        byte[] result = null;
        try {
            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(file);
            //移动到制定位置
            fis.skip(offset);
            int i = 0;
            while ((i = fis.read(buff)) > 0) {
                baos.write(buff, 0, i);
            }
            result = baos.toByteArray();
            fis.close();
            baos.close();
        } catch (Throwable e){
            throw new LedgerException(e);
        }
        return result;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
