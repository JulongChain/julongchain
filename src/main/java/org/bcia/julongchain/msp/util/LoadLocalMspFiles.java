/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.msp.util;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.csp.gm.dxct.util.CryptoUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 加载本地msp文件夹
 *
 * @author zhangmingyang
 * @Date: 2018/4/4
 * @company Dingxuan
 */
public class LoadLocalMspFiles {
    private static JavaChainLog log = JavaChainLogFactory.getLog(LoadLocalMspFiles.class);
    File or;
    File[] files;
    /**
     * 文件名集合
     */
    List<String> pathName = new ArrayList<String>();
    /**
     * 存放文件名和文件的绝对路径
     */
    private static HashMap<String, String> map = new HashMap<String, String>();
    /**
     * 存放文件的绝对路径和文件具体的值
     */
    public static HashMap<String, String> mspMap = new HashMap<String, String>();
    /**
     * 存在文件的相对路径和文件具体的值
     */
    public static  HashMap<String,String> pemMap=new HashMap<String,String>();
    public static final String ADMINCERTS = "admincerts";
    public static final String CACERTS= "cacerts";
    public static final String CRLSFOLDER = "crls";
    public static final String KEYSTORE = "keystore";
    public static final String SIGNCERTS = "signcerts";
    public static final String TLSCACERTS = "tlscacerts";
    public static final String TLSINTERMEDIATECERTS = "tlsintermediatecerts";
    public static final String INTERMEDIATECERTS = "intermediatecerts";
    public static  final String CONFIGFILENAME="config.yaml";

    /**
     *  从路经中获取证书内容集合
     * @param dir
     * @return
     */
    public List<byte[]> getCertFromDir(String dir){
        String fileAbolutePath;
        List<byte[]> fileContent=new ArrayList<>();
        or = new File(dir);
        files = or.listFiles();
        if (files != null) {


            for(int i=0;i<files.length;i++){
                if(files[i].isFile()){
                    fileAbolutePath =files[i].getAbsolutePath();
                    try {
                     byte[]  pemBytes= FileUtils.readFileBytes(fileAbolutePath);
                        fileContent.add(pemBytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(files[i].isDirectory()){
                    getCertFromDir(files[i].getAbsolutePath());
                }

            }
        }
        return  fileContent;
    }

    public List<byte[]> getSkFromDir(String dir){
        String fileAbolutePath;
        List<byte[]> fileContent=new ArrayList<>();
        or = new File(dir);
        files = or.listFiles();
        if (files != null) {


            for(int i=0;i<files.length;i++){
                if(files[i].isFile()){
                    fileAbolutePath =files[i].getAbsolutePath();
                    try {
                        byte[]  pemBytes=CryptoUtil.readSkFile(fileAbolutePath);
                        fileContent.add(pemBytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(files[i].isDirectory()){
                    getSkFromDir(files[i].getAbsolutePath());
                }

            }
        }
        return  fileContent;
    }
}
