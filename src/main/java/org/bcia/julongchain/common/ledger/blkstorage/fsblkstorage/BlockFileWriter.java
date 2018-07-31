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
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.io.*;

/**
 * 写入文件
 *
 * @author sunzongyu
 * @date 2018/04/12
 * @company Dingxuan
 */
public class BlockFileWriter {
	private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockFileWriter.class);

    private String filePath;
    private File file;

    public BlockFileWriter(String filePath) throws LedgerException{
        this.filePath = filePath;
        this.file = new File(filePath);
        open();
    }

    /**
     * 截断文件为指定大小
	 * 主要用于在文件写入出现异常时恢复文件
     */
    public void truncateFile(int targetSize) throws LedgerException {
        if(file.length() <= targetSize){
            return;
        }
        FileInputStream fis;
        FileOutputStream fos;
        try {
            fis = new FileInputStream(file);
            byte[] inputBytes = new byte[targetSize];
            int read = fis.read(inputBytes);
			if (read != targetSize) {
				logger.debug("Can not read specified size. Expected file size [{}], actual file size [{}]", targetSize, read);
			}
            fis.close();
            fos = new FileOutputStream(file);
            fos.write(inputBytes);
            fos.close();
        } catch (Throwable e) {
            throw new LedgerException(e);
        }
    }

	/**
	 * 将字节b写入文件
	 */
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

	/**
	 * 打开文件（文件不存在时创建文件）
	 */
	public void open() throws LedgerException{
        if (!IoUtil.createFileIfMissing(filePath)) {
            throw new LedgerException("Can not create file " + filePath);
        }
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
