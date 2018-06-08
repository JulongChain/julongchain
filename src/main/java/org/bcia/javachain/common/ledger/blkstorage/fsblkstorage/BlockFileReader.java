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
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;

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

    public BlockFileReader(String filePath){
        this.file = new File(filePath);
    }

    /**
     * 从offset位起,读取length字节
     */
    public byte[] read(long offset, long length) throws LedgerException {
		FileInputStream fis;
		byte[] result = null;
		try {
			result = new byte[(int) length];
			fis = new FileInputStream(file);
			//移动到指定位置
			fis.skip(offset);
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

    public static void main(String[] args) throws Exception {
        BlockFileReader reader = new BlockFileReader(LedgerConfig.getBlockStorePath() + "/chains/myGroup/blockfile_000000");
		byte[] read = reader.read(1098, 7);
		soutBytes(read);
	}

    public static void soutBytes(byte[] bytes){
        int i = 0;
        for(byte b : bytes){
            System.out.print(b + ",");
            if (i++ % 30 == 29) {
                System.out.println();
                System.out.println(i);
            }
        }
        System.out.println();
    }
}
