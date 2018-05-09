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
import org.bcia.javachain.core.common.smartcontractprovider.SDSData;

import java.io.*;

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

    /**
     * 创建文件
     * @param filePath
     * @return
     */
    public static boolean createFileIfMissing(String filePath) throws Exception{
        boolean result = false;
        File file = new File(filePath);
        if(file.exists()){
            return true;
        }
        String fileDir = filePath.substring(0, filePath.lastIndexOf(File.separator));
        File dir = new File(fileDir);
        if(!dir.exists()){
            dir.mkdir();
        }
        result = file.createNewFile();
        return result;
    }

    /**
     * 序列化对象
     * @param serializable
     * @return
     * @throws IOException
     */
    public static byte[] obj2ByteArray(Serializable serializable) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(serializable);
            oos.flush();
            byte[] result = baos.toByteArray();
            baos.close();
            oos.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 反序列化对象
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object byteArray2Obj(byte[] bytes){
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            bais.close();
            ois.close();
            return obj;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
