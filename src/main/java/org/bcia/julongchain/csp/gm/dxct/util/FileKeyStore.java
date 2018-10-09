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
package org.bcia.julongchain.csp.gm.dxct.util;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.csp.gm.dxct.IKeyStore;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2Key;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhangmingyang
 * @Date: 2018/5/3
 * @company Dingxuan
 */
public class FileKeyStore implements IKeyStore {
    private static JulongChainLog log = JulongChainLogFactory.getLog(FileKeyStore.class);
    /**
     * 所有文件的名称
     */
    private static List<String> filesName = new ArrayList<String>();
    public static HashMap<String, String> filePathMap = new HashMap<String, String>();
    private String path;
    private boolean isOpen;
    private byte[] pwd;


    public FileKeyStore(String path, boolean isOpen, byte[] pwd) {
        this.path = path;
        this.isOpen = isOpen;
        this.pwd = pwd;
    }


    /**
     * 初始化
     *
     * @param pwd
     * @param path
     * @param readOnly
     */
    private static void init(byte[] pwd, String path, boolean readOnly) {
        if (path == "") {
            try {
                log.error("An invalid KeyStore path provided. Path cannot be an empty string.");
                throw new JulongChainException("An invalid KeyStore path provided. Path cannot be an empty string.");
            } catch (JulongChainException e) {
                e.getMessage();
            }
        }

    }


    @Override
    public boolean readOnly() {
        return false;
    }

    @Override
    public IKey getKey(byte[] ski) throws CspException {
        if (ski.length == 0) {
            log.error("Invalid SKI. Cannot be of zero length.");
        }
        String suffix = getSuffix(Hex.toHexString(ski));
        System.out.println(suffix);
        switch (suffix) {
            case "pk":
                //IKey key= (IKey) loadPrivateKey(suffix);
                //return  key;
            case "sk":
                IKey key = loadPrivateKey(Hex.toHexString(ski));
                log.info(Hex.toHexString(key.toBytes()));
                return key;
            default:

        }

        return null;
    }

    @Override
    public void storeKey(IKey ikey) {

    }

    private String getSuffix(String alisas) {
        iteratorPath(this.path);
        for (String file : filesName) {
            if (file.equalsIgnoreCase(alisas + "_sk")) {
                return "sk";
            }
            if (file.equalsIgnoreCase(alisas + "_pk")) {
                return "pk";
            }
            if (file.equalsIgnoreCase(alisas + "_key")) {
                return "key";
            }
        }
        return "";
    }


    /**
     * 迭代文件夹
     *
     * @param dir
     */
    public void iteratorPath(String dir) {

        File or = new File(dir);
        File[] files = or.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    filesName.add(file.getName());
                    filePathMap.put(file.getName(), file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    iteratorPath(file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 读取私钥文件
     *
     * @param alias
     * @return
     */
    public SM2Key loadPrivateKey(String alias) {

        String filePath = filePathMap.get(alias + "_sk");
        File inFile = new File(filePath);
        long fileLen = inFile.length();
        Reader reader = null;
        PemObject pemObject = null;
        try {
            reader = new FileReader(inFile);

            char[] content = new char[(int) fileLen];
            reader.read(content);
            String str = new String(content);

            StringReader stringreader = new StringReader(str);
            PemReader pem = new PemReader(stringreader);
            pemObject = pem.readPemObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        pemObject.getContent();
        return new SM2Key();
    }

}
