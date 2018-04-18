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

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;

/**
 * 读取文件
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockfileReader {

    private File file;

    public static BlockfileReader newBlockfileReader(String filePath){
        BlockfileReader reader = new BlockfileReader();
        reader.file = new File(filePath);
        return reader;
    }

    public byte[] read(Integer offset, Integer length) throws LedgerException {
        FileInputStream fis;
        byte[] result = null;
        try {
            result = new byte[(int) file.length()];
            fis = new FileInputStream(file);
            fis.read(result);
            fis.close();
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
