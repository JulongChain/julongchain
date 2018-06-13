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

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

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
	private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockFileReader.class);
    private File file;

    public BlockFileReader(String filePath){
        this.file = new File(filePath);
    }

    /**
     * 从offset位起,读取length字节
     */
    public byte[] read(long offset, long length) throws LedgerException {
		FileInputStream fis;
		byte[] result;
		try {
			result = new byte[(int) length];
			fis = new FileInputStream(file);
			//移动到指定位置
			long skip = fis.skip(offset);
			if (skip != offset) {
				logger.debug("Wrong file skip. Expect skip = [{}], actual skip = [{}]", offset, skip);
			}
			int read = fis.read(result);
			if (read != length) {
				logger.debug("Wrong file read. Except read = [{}], actual read = [{}]", length, read);
			}
			fis.close();
		} catch (Throwable e){
			logger.error(e.getMessage(), e);
			throw new LedgerException(e);
		}
		return result;
    }

    public void close(){
    	//do nothing
	}

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
