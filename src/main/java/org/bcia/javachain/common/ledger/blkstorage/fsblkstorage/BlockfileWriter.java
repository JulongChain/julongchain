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
import org.bcia.javachain.core.node.NodeConfig;

import java.io.*;

/**
 * 写入文件
 *
 * @author sunzongyu
 * @date 2018/04/12
 * @company Dingxuan
 */
public class BlockfileWriter {

    private String filePath;
    private File file;

    public static BlockfileWriter newBlockfileWriter(String filePath){
        BlockfileWriter writer = new BlockfileWriter();
        writer.setFilePath(filePath);
        return writer.open();
    }

    /**
     * 截断文件为指定大小
     */
    public void truncateFile(Integer targetSize) throws LedgerException {
        if(file.length() <= targetSize){
            return;
        }
        FileInputStream fis;
        FileOutputStream fos;
        try {
            fis = new FileInputStream(file);
            byte[] inputBytes = new byte[targetSize];
            fis.read(inputBytes);
            fis.close();
            fos = new FileOutputStream(file);
            fos.write(inputBytes);
            fos.close();
        } catch (Throwable e) {
            throw new LedgerException(e);
        }
    }

    public void append(byte[] b, Boolean sync) throws LedgerException {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file, true);
            fos.write(b);
            fos.close();
        } catch (IOException e) {
            throw new LedgerException(e);
        }
    }

    public BlockfileWriter open() {
        file = new File(filePath);
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
