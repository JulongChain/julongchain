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
package org.bcia.julongchain.common.ledger.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * io操作辅助类
 *
 * @author wanliangbing, sunzongyu
 * @date 2018/3/9
 * @company Dingxuan
 */
public class IoUtil {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(IoUtil.class);
    private static final int BUFFER = 1024;

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
    public static List<String> listSubdirs(String dirPath) {
        List<String> list = new ArrayList<>();
        File dir = new File(dirPath);
        if(!dir.exists()){
            logger.debug("Dir {} is not exists", dir);
            return null;
        }
        for (File file : dir.listFiles()) {
            if(file.isDirectory()){
                list.add(file.getName());
            }
        }
        return list;
    }

    public static void logDirStatus(String msg, String dirPath) {
        return;
    }

    /**
     * 修改文件权限 755
     * 默认文件存在
     */
    public static void chmod(File file, int perm){
        int uMod = perm / 100;
        int gMod = (perm % 100) / 10;
        int oMod = perm % 10;
        if(uMod > 7 || uMod < 0){
            logger.error("Wrong mod type " + perm);
            return;
        }
        if(gMod > 7 || gMod < 0){
            logger.error("Wrong mod type " + perm);
            return;
        }
        if(oMod > 7 || oMod < 0){
            logger.error("Wrong mod type " + perm);
            return;
        }
        if (!file.setWritable(uMod >= 4, (uMod > 0 && uMod < 5))) {
            logger.error("Can not set write permission to dir " + file.getAbsolutePath());
        }
        if (!file.setReadable(gMod >= 4, (uMod > 0 && uMod < 5))) {
            logger.error("Can not set read permission to dir " + file.getAbsolutePath());
        }
        if (!file.setExecutable(oMod >= 4, (uMod > 0 && uMod < 5))) {
            logger.error("Can not set execute permission to dir " + file.getAbsolutePath());
        }
    }

    /**
     * 创建目录
     */
    public static boolean createDirIfMissing(String dirPath){
        File dir = new File(dirPath);
        if(dir.exists()){
            logger.debug("Dir [{}] is already exists", dirPath);
            return true;
        }
        if (!dir.mkdirs()) {
            logger.debug("Can not create dir [" + dirPath + "]");
            return false;
        }
        chmod(dir, 644);
        logger.debug("Create dir [{}] success", dir.getAbsolutePath());
        return true;
    }

    /**
     * 创建文件
     */
    public static boolean createFileIfMissing(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            logger.debug("File [{}] is already exists", filePath);
            return true;
        }
        File dir = file.getParentFile();
        if (!createDirIfMissing(dir.getAbsolutePath())) {
            logger.debug("Can not create dir [" + dir.getAbsolutePath() + "]");
            return false;
        }
        try {
            if (file.createNewFile()) {
                chmod(file, 755);
                logger.debug("Create file [{}] success", filePath);
                return true;
            } else {
                logger.debug("Can not create file [" + filePath + "]");
                return false;
            }
        } catch (IOException e) {
            logger.error("Got error error:{} when createing file:[{}]", e.getMessage(), filePath);
            return false;
        }
    }

    /**
     * 序列化对象
     * @param serializable
     * @return
     * @throws JavaChainException
     */
    public static byte[] obj2ByteArray(Serializable serializable) throws JavaChainException {
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
            logger.error(e.getMessage(), e);
            throw new JavaChainException(e);
        }
    }

    /**
     * 反序列化对象
     * @param bytes
     * @return
     * @throws JavaChainException
     */
    public static Object byteArray2Obj(byte[] bytes) throws JavaChainException{
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
            logger.error(e.getMessage(), e);
            throw new JavaChainException(e);
        }
    }
    /**
     * 获取文件价下所有文件相对文件夹路径
     * @param path 文件夹路径
     * @return
     */
    public static Map<String, File> getFileRelativePath(String path){
        File file = new File(path);
        if(!file.exists()){
            logger.debug("File {} is not exists", path);
            return null;
        }
        if(!file.isDirectory()){
            logger.debug("Input must be a directory, but file {} is not", path);
            return null;
        }
        Map<String, File> result = new TreeMap<>(Comparator.naturalOrder());
        Queue<File> queue = new LinkedList<>();
        queue.add(file);
        while(queue.size() > 0){
            File tmpFile = queue.poll();
            if(tmpFile.isDirectory()){
                File[] tmpFiles = tmpFile.listFiles();
                if (tmpFiles != null) {
                    queue.addAll(Arrays.asList(tmpFiles));
                }
            } else if(tmpFile.isFile()) {
                result.put(tmpFile.getAbsolutePath().substring(file.getAbsolutePath().length() + 1), tmpFile);
            }
        }
        return result;
    }

    /**
     * 关闭流
     */
    public static void closeStream(Closeable... closeables) throws JavaChainException{
        try {
            for (Closeable closeable : closeables) {
                if(closeable != null){
                    closeable.close();
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new JavaChainException(e);
        }
    }

    /**
     * 将文件制作tar包
     * @param files 文件集合
     *              key:文件路径
     *              value:文件对象
     * @param cache 缓冲区大小
     * @return tar 字节流
     */
    public static byte[] tarWriter(Map<String, File> files, int cache) throws JavaChainException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TarArchiveOutputStream taos = new TarArchiveOutputStream(baos);
        taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        FileInputStream fis = null;
        TarArchiveEntry tae = null;
        try {
            for (Map.Entry<String, File> entry : files.entrySet()) {
                String fileName = entry.getKey();
                File file = entry.getValue();
                fis = new FileInputStream(file);
                tae = new TarArchiveEntry(file);
                tae.setName(fileName);
                taos.putArchiveEntry(tae);
                int num = 0;
                byte[] buff = new byte[cache];
                while((num = fis.read(buff)) != -1){
                    taos.write(buff, 0, num);
                }
                taos.closeArchiveEntry();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new JavaChainException(e);
        } finally {
            closeStream(fis, taos, baos);
        }
        return baos.toByteArray();
    }

    /**
     * 使用gzip压缩文件
     * @param fileBytes 将要压缩的文件流
     * @return gzip压缩的文件流
     */
    public static byte[] gzipWriter(byte[] fileBytes) throws JavaChainException{
        ByteArrayOutputStream baos = null;
        GZIPOutputStream gzos = null;
        try {
            baos = new ByteArrayOutputStream();
            gzos = new GZIPOutputStream(baos);
            gzos.write(fileBytes, 0, fileBytes.length);
            gzos.finish();
            gzos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new JavaChainException(e);
        } finally {
            closeStream(baos, gzos);
        }
        return baos.toByteArray();
    }

    /**
     * 解压缩tar包
     * @param tarBytes tar包字节流
     * @param cache 缓冲大小
     * @return 文件集合
     *          key:文件相对路径
     *          value:文件流
     */
    public static Map<String, byte[]> tarReader(byte[] tarBytes, int cache) throws JavaChainException{
        Map<String, byte[]> result = new HashMap<>();
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;
        TarArchiveInputStream tais = null;
        try {
            baos = new ByteArrayOutputStream();
            bais = new ByteArrayInputStream(tarBytes);
            tais = new TarArchiveInputStream(bais);
            TarArchiveEntry tae = null;
            while((tae = tais.getNextTarEntry()) != null){
                int len = 0;
                byte[] buff = new byte[cache];
                while((len = tais.read(buff)) != -1){
                    baos.write(buff, 0, len);
                }
                result.put(tae.getName(), baos.toByteArray());
                baos.reset();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new JavaChainException(e);
        } finally {
            closeStream( tais, bais, baos);
        }
        return result;
    }

    /**
     * 使用gzip解压文件
     * @param fileBytes 文件流
     * @param cache 缓冲大小
     * @return 解压后文件流
     */
    public static byte[] gzipReader(byte[] fileBytes, int cache) throws JavaChainException{
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;
        GZIPInputStream gzis = null;
        try {
            bais = new ByteArrayInputStream(fileBytes);
            gzis = new GZIPInputStream(bais);
            baos = new ByteArrayOutputStream();
            byte[] buff = new byte[cache];
            int num = 0;
            while((num = gzis.read(buff)) > 0){
                baos.write(buff, 0, num);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new JavaChainException(e);
        } finally {
            closeStream( bais, baos, gzis);
        }
        return baos.toByteArray();
    }

    /**
     * 输出解压缩的文件
     * @param files 文件集合
     *              key: 文件相对路径
     *              value: 文件流
     * @param outputPath 输出路径
     */
    public static void fileWriter(Map<String, byte[]> files, String outputPath) throws JavaChainException {
        outputPath = outputPath.endsWith(File.separator) ? outputPath : outputPath + File.separator;
        FileOutputStream fos = null;
        try {
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                String fileRelativePath = entry.getKey();
                byte[] fileBytes = entry.getValue();
                File file = new File(outputPath + fileRelativePath);
                File dir = file.getParentFile();
                if (!createDirIfMissing(dir.getAbsolutePath())) {
                    throw new JavaChainException("Can not create dir " + dir.getAbsolutePath());
                }
                fos = new FileOutputStream(file);
                fos.write(fileBytes);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new JavaChainException(e);
        } finally {
            closeStream(fos);
        }
    }

    /**
     * 将文件读取为流形式
     * @param filePath 文件路径
     * @param cache 缓冲大小
     */
    public static byte[] fileReader(String filePath, int cache) throws JavaChainException{
        File file = new File(filePath);
        if (!file.exists()) {
            logger.debug("File {} not found", filePath);
            return null;
        }
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try{
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            int num = 0;
            byte[] bytes = new byte[cache];
            while((num = fis.read(bytes)) > 0){
                baos.write(bytes, 0, num);
            }
            return baos.toByteArray();
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new JavaChainException(e);
        } finally {
            closeStream(fis, baos);
        }
    }
}
