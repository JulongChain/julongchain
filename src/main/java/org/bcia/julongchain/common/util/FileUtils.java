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
package org.bcia.julongchain.common.util;

import org.apache.commons.io.IOUtils;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.io.*;

/**
 * 文件处理工具类
 *
 * @author zhouhui
 * @date 2018/3/2
 * @company Dingxuan
 */
public class FileUtils {
    private static JavaChainLog log = JavaChainLogFactory.getLog(FileUtils.class);

    /**
     * 将文件读写成字节数组
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] readFileBytes(String filePath) throws IOException {
        InputStream is = null;

        try {
            is = new FileInputStream(filePath);
            byte[] bytes = IOUtils.toByteArray(is);
            return bytes;
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 将字节数组写入文件
     *
     * @param filePath
     * @param bytes
     */
    public static void writeFileBytes(String filePath, byte[] bytes) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
            os.write(bytes);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 判断文件路径是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 删除文件夹
     * @param dir
     * @return 删除是否成功
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}
