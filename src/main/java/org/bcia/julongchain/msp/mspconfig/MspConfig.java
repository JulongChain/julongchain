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

/**
 * @author zhangmingyang
 * @Date: 2018/3/29
 * @company Dingxuan
 */
public class MspConfig {
    public static final String MspConfig_FILE_PATH = "gmcsp.yaml";
    private static final MspConfig DEFAULT_INSTANCE;
    private static  final String DEFAULT="GM";
    private static  final String SYMMETRIKEY="SM4";
    private static  final String SIGN="SM2";
    private static  final String HASH="SM3";
    private static  final String ASYMMETRIC="SM2";
    private static  final String SECURITY="256";
    private static  final String PUBLICKEYSTORE="D:/msp/keystore/publickey.pem";
    // private static  final String PUBLICKEYSTORE="/opt/msp/keystore/publickey.pem";
    private static  final String PRIVATEKEYSTORE="D:/msp/keystore/privatekey.pem";
    // private static  final String PRIVATEKEYSTORE="/opt/msp/keystore/privatekey.pem";
    private static  final String MSPCONFIGPATH="msp";
    private static  final String LOCALMSPID="DEFAULT";
    private static  final String LOCALMSPTYPE="GMMSP";


    static {

        FileKeyStore defaultFileKeyStore=new FileKeyStore();
        defaultFileKeyStore.setPrivateKeyStore(PRIVATEKEYSTORE);
        defaultFileKeyStore.setPublicKeyStore(PUBLICKEYSTORE);

        GM defaultGm=new GM();
        defaultGm.setSymmetricKey(SYMMETRIKEY);
        defaultGm.setSign(SIGN);
        defaultGm.setHash(HASH);
        defaultGm.setAsymmetric(ASYMMETRIC);
        defaultGm.setSecurity(SECURITY);
        defaultGm.setFileKeyStore(defaultFileKeyStore);

        Csp defaultCsp=new Csp();
        defaultCsp.setDefaultValue(DEFAULT);
        defaultCsp.setGm(defaultGm);

        Node defaultNode=new Node();
        defaultNode.setLocalMspId(LOCALMSPID);
        defaultNode.setMspConfigPath(MSPCONFIGPATH);
        defaultNode.setLocalMspType(LOCALMSPTYPE);
        defaultNode.setCsp(defaultCsp);

        DEFAULT_INSTANCE=new MspConfig();
        DEFAULT_INSTANCE.setNode(defaultNode);
    }

    public Node node;

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public static class Node {
       private Csp  csp;
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
        private GM gm;

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public GM getGm() {
            return gm;
        }

        public void setGm(GM gm) {
            this.gm = gm;
        }
    }
    public static  class GM{
        private String symmetricKey;
        private String sign;
        private String hash;
        private String asymmetric;
        private String security;
        private FileKeyStore fileKeyStore;

        public String getSymmetricKey() {
            return symmetricKey;
        }

        public void setSymmetricKey(String symmetricKey) {
            this.symmetricKey = symmetricKey;
        }

        public FileKeyStore getFileKeyStore() {
            return fileKeyStore;
        }

        public void setFileKeyStore(FileKeyStore fileKeyStore) {
            this.fileKeyStore = fileKeyStore;
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
    public static class  FileKeyStore{
        private String publicKeyStore;
        private String privateKeyStore;

        public String getPublicKeyStore() {
            return publicKeyStore;
        }

        public void setPublicKeyStore(String publicKeyStore) {
            this.publicKeyStore = publicKeyStore;
        }

        public String getPrivateKeyStore() {
            return privateKeyStore;
        }

        public void setPrivateKeyStore(String privateKeyStore) {
            this.privateKeyStore = privateKeyStore;
        }
    }
}
