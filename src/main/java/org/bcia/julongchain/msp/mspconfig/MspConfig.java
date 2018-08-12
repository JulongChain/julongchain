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
package org.bcia.julongchain.msp.mspconfig;

import java.util.Map;

/**
 * 将gmcsp文件转换为配置对象
 * @author zhangmingyang
 * @Date: 2018/6/13
 * @company Dingxuan
 */
public class MspConfig {
    public static final String MspConfig_FILE_PATH = "gmcsp.yaml";

    public Node node;

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public static class Node {
        private Csp csp;
        private String mspConfigPath;
        private String localMspId;
        private String localMspType;

        public Csp getCsp() {
            return csp;
        }

        public void setCsp(Csp csp) {
            this.csp = csp;
        }

        public String getMspConfigPath() {
            return mspConfigPath;
        }

        public void setMspConfigPath(String mspConfigPath) {
            this.mspConfigPath = mspConfigPath;
        }

        public String getLocalMspId() {
            return localMspId;
        }

        public void setLocalMspId(String localMspId) {
            this.localMspId = localMspId;
        }

        public String getLocalMspType() {
            return localMspType;
        }

        public void setLocalMspType(String localMspType) {
            this.localMspType = localMspType;
        }
    }

    public  static class Csp{
        private String defaultValue;
        private Map<String,Map<String,String>> factoryOpts;

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Map<String, Map<String, String>> getFactoryOpts() {
            return factoryOpts;
        }

        public void setFactoryOpts(Map<String, Map<String, String>> factoryOpts) {
            this.factoryOpts = factoryOpts;
        }
    }
    public static  class GM{
        private String symmetricKey;
        private String sign;
        private String hash;
        private String asymmetric;
        private String security;


        public String getSymmetricKey() {
            return symmetricKey;
        }

        public void setSymmetricKey(String symmetricKey) {
            this.symmetricKey = symmetricKey;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getAsymmetric() {
            return asymmetric;
        }

        public void setAsymmetric(String asymmetric) {
            this.asymmetric = asymmetric;
        }

        public String getSecurity() {
            return security;
        }

        public void setSecurity(String security) {
            this.security = security;
        }


    }
}
