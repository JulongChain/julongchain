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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.io.IOUtils;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class IoUtil {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(IoUtil.class);
    private static final int BUFFER = 1024;

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
            throw new JavaChainException(e);
        }
    }

    /**
     * 将文件打包为.tar.gz
     * @param sourceFile 源文件
     * @param outPutPath 输出文件夹
     * @param fileName 输出文件名
     * @throws JavaChainException
     */
    public static void tarGzWriter(String sourceFile, String outPutPath, String fileName) throws JavaChainException{
        List<File> l = new ArrayList<>();
        File source = new File(sourceFile);
        if(source.isDirectory()){
            l = listFiles(source);
        } else {
            l.add(source);
        }
        tarWriter(l, outPutPath, fileName);
    }

    /**
     * 将文件中所有文件加入list
     * @param sourceFile 源文件夹
     * @return 包含所有文件的list
     */
    private static List<File> listFiles(File sourceFile){
        Queue<File> q = new LinkedList<>();
        List<File> l = new ArrayList<>();
        q.add(sourceFile);
        while(q.size() > 0){
            File file = q.poll();
            if (file.isDirectory()) {
                q.addAll(Arrays.asList(file.listFiles()));
            } else {
                l.add(file);
            }
        }
        return l;
    }

    /**
     * 将所有文件打tar包
     * @param files 需要打包的文件
     * @param outPutPath 输出文件路径
     * @param fileName 输出文件名
     * @return tar文件
     */
    public static File tarWriter(List<File> files, String outPutPath, String fileName){
        File outPutFile = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        GZIPOutputStream gzp = null;
        File tar = new File(outPutPath + File.separator + "temp.tar");
        try {
            fis = new FileInputStream(gzipWriter(files, tar));
            bis = new BufferedInputStream(fis, BUFFER);
            outPutFile = new File(outPutPath + "/" + checkFileName(fileName) + ".tar.gz");
            fos = new FileOutputStream(outPutFile);
            gzp = new GZIPOutputStream(fos);
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                gzp.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(gzp != null){
                    gzp.finish();
                    gzp.flush();
                    gzp.close();
                }
                if(fos != null){
                    fos.close();
                }
                if(bis != null){
                    bis.close();
                }
                if(fis != null){
                    fis.close();
                }
                if(tar.exists()){
                    tar.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return outPutFile;
    }

    /**
     * 压缩文件
     * @param files 所需压缩的文件
     * @param target 压缩后目标文件
     * @return 目标文件对象
     */
    public static File gzipWriter(List<File> files, File target){
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        TarArchiveOutputStream taos = null;
        FileInputStream fis = null;
        TarArchiveEntry entry = null;
        try {
            fos  = new FileOutputStream(target);
            bos = new BufferedOutputStream(fos, BUFFER);
            taos = new TarArchiveOutputStream(bos);
            //解决文件名过长问题
            taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
            int i = 0;
            for(File file : files){
                entry = new TarArchiveEntry(file);
                entry.setName(file.getName());
                i++;
                taos.putArchiveEntry(entry);
                fis = new FileInputStream(file);
                IOUtils.copy(fis, taos);
                taos.closeArchiveEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(fis != null){
                    fis.close();
                }
                if(taos != null){
                    taos.finish();
                    taos.flush();
                    taos.close();
                }
                if(bos != null){
                    bos.flush();
                    bos.close();
                }
                if(fos != null){
                    fos.flush();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return target;
    }

    /**
     * 读取.tar.gz文件
     * @param sourceFile 源文件
     * @param targetDir 目标目录
     * @throws JavaChainException
     */
    public static void tarGzReader(String sourceFile, String targetDir) throws JavaChainException{
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        GZIPInputStream gzis = null;
        TarArchiveInputStream tais = null;
        OutputStream os = null;
        try {
            File tarGzFile = new File(sourceFile);
            String tarGzFileName = tarGzFile.getName();
            //目标文件扩展名错误
            checkFileName(tarGzFileName);
            //目标目录不存在时创建新目录
            File targetDirFile = new File(targetDir);
            if(!targetDirFile.exists()){
                if(!targetDirFile.mkdir()){
                    throw new JavaChainException("Fail to create file " + targetDir);
                }
                if(!targetDirFile.setWritable(true, false)){
                    throw new JavaChainException("Fail to set file writable " + targetDir);
                }
            }
            fis = new FileInputStream(tarGzFile);
            bis = new BufferedInputStream(fis);
            gzis = new GZIPInputStream(bis);
            tais = new TarArchiveInputStream(gzis);
            TarArchiveEntry tae = null;
            //循环读取文件并写入目标文件
            while ((tae = tais.getNextTarEntry()) != null) {
                File tmpFile = new File(targetDir + File.separator + tae.getName());
                os = new FileOutputStream(tmpFile);
                int length = 0;
                byte[] b = new byte[BUFFER];
                while ((length = tais.read(b)) != -1) {
                    os.write(b, 0, length);
                }
            }
        } catch (IOException e) {
            throw new JavaChainException(e);
        } finally {

            try {
                if (fis != null) {
                    fis.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (gzis != null) {
                    gzis.close();
                }
                if (tais != null) {
                    tais.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                throw new JavaChainException(e);
            }
        }
    }

    private static String checkFileName(String fileName) throws JavaChainException{
        String fileNameEnding = ".tar.gz";
        if(fileName.length() > fileNameEnding.length() && fileName.substring(fileName.length() - fileNameEnding.length()).equals(fileNameEnding)){
            return fileName.substring(0, fileName.length() - fileNameEnding.length());
        } else {
            throw new JavaChainException("Wrong file name " + fileName);
        }
    }

    public static void main(String[] args) throws JavaChainException {
//        tarGzWriter("/home/bcia/123/4-11", "/home/bcia/123", "4-1.tar.gz");
        tarGzReader("/home/bcia/123/4-1.tar.gz", "/home/bcia/4-111");
    }
}
