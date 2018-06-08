package org.bcia.julongchain.consenter.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/4/2
 * @company Dingxuan
 */
public class LoadYamlTest {

    @Test
    public void readYamlFile() {
        Map map=LoadYaml.readYamlFile("gmcsp.yaml");
        System.out.println(map.get("node"));
        System.out.println(((HashMap)map.get("node")).get("CSP"));
        System.out.println( ((HashMap)((HashMap) map.get("node")).get("CSP")).get("GM"));
        System.out.println(((HashMap)((HashMap)((HashMap) map.get("node")).get("CSP")).get("GM")).get("FileKeyStore"));
        System.out.println( ((HashMap) ((HashMap)((HashMap)((HashMap) map.get("node")).get("CSP")).get("GM")).get("FileKeyStore")).get("PublicKeyStore"));
        String mspType= (String) ((HashMap)map.get("node")).get("localMspType");
        String localmspdir=(String) ((HashMap)map.get("node")).get("mspConfigPath");
        String mspID=(String) ((HashMap)map.get("node")).get("localMspId");

        System.out.println(mspType);
        System.out.println(localmspdir);
        System.out.println(mspID);
    }
}