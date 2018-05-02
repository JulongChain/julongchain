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
package org.bcia.javachain.common.ledger.util;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

import java.io.File;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class IoUtil {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(IoUtil.class);

    /** CreateDirIfMissing creates a dir for dirPath if not already exists. If the dir is empty it returns true
     *
     * @param dirPath
     * @return
     */
    public static Boolean createDirIfMissing(String dirPath) {
        try {
            File file = new File(dirPath);
            if(!file.exists()){
                file.mkdir();
            }
        } catch (Exception e) {
            logger.debug(String.format("Creating [%s] failed", dirPath));
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /** DirEmpty returns true if the dir at dirPath is empty
     *
     * @param dirPath
     * @return
     */
    public static Boolean DirEmpty(String dirPath){
        return Boolean.FALSE;
    }

    /**
     * 返回-1:文件不存在
     * >=0:文件大小
     */
    public static long fileExists(String filePath) {
        File file = new File(filePath);
        if(!file.exists()){
            return -1;
        }
        return file.length();
    }

    /** ListSubdirs returns the subdirectories
     *
     * @param dirPath
     * @return
     */
    public static String[] listSubdirs(String dirPath) {
        return null;
    }

    public static void logDirStatus(String msg, String dirPath) {
        return;
    }

}
