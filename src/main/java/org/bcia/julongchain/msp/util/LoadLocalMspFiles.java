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
import org.bcia.julongchain.csp.gm.dxct.sm2.util.SM2KeyUtil;

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
     * 遍历文件夹
     *
     * @param dir
     */
    public void iteratorPath(String dir) {
        or = new File(dir);
        files = or.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    //文件名和文件路径放入map
                    map.put(file.getName(), file.getAbsolutePath());
                    pathName.add(file.getName());
                } else if (file.isDirectory()) {
                    iteratorPath(file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 初始化msp文件夹
     *
     * @param
     */
    public static void init(String mspPath) {
        LoadLocalMspFiles configBuilder = new LoadLocalMspFiles();
        configBuilder.iteratorPath(mspPath);
        for (String list : configBuilder.pathName) {
            log.info("文件路径:" + map.get(list));
            try {
                //文件绝对路径和文件内容放入mspMap
                mspMap.put(map.get(list), SM2KeyUtil.readFile(map.get(list)));
                mspMap.put(map.get(list), list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取密码材料内容
     * @param dir  文件路径
     * @return 字节数组list
     */

    public List<String>  getPemMaterialFromDir(String dir) {
        String fileAbolutePath;
        List<String> fileContent=new ArrayList<String>();
        or = new File(dir);
        files = or.listFiles();
        if (files != null) {


            for(int i=0;i<files.length;i++){
                if(files[i].isFile()){
                    fileAbolutePath =files[i].getAbsolutePath();
                  //  System.out.println("文件的绝对路径："+fileAbolutePath);
                    try {
                      String file=  SM2KeyUtil.readFile(fileAbolutePath);
                      fileContent.add(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(files[i].isDirectory()){
                    getPemMaterialFromDir(files[i].getAbsolutePath());
                }

            }
      }
        return  fileContent;
    }

    /**
     * 通过文件相对路径获取文件内容
     * @param dir
     */
    public  void getFileContent(String dir){
        String fileAbolutePath;
        or = new File(dir);
        files = or.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileAbolutePath =file.getAbsolutePath();
                    log.info("文件的绝对路径："+fileAbolutePath);
                    try {
                        pemMap.put(file.getParent(),SM2KeyUtil.readFile(fileAbolutePath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (file.isDirectory()) {
                    getPemMaterialFromDir(file.getAbsolutePath());
                }

            }
        }
    }

}
