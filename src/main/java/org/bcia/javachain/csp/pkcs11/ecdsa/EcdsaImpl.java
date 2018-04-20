/**
 * Copyright Feitian. All Rights Reserved.
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
package org.bcia.javachain.csp.pkcs11.ecdsa;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.csp.pkcs11.IPKCS11FactoryOpts;
import org.bcia.javachain.csp.pkcs11.util.PKCS11CspKey;
import sun.security.pkcs11.wrapper.*;
import sun.security.util.ECUtil;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import java.text.SimpleDateFormat;

/**
 * Class description
 *
 * @author XuYing
 * @date 4/19/18
 * @company FEITIAN
 */

//Ecdsa Impl
public class EcdsaImpl {

    // 生成ecdsa密钥
    public static class generateECKey extends EcdsaImpl{
        private byte[] byteSKI;
        private IKey ikey;

        /**
         * 生成ecdsa密钥
         *
         * @param keySize     模长
         * @param ephemeral   临时标记
         * @param opts        p11 factory
         * @return
         */
        public generateECKey(int keySize, boolean ephemeral, IPKCS11FactoryOpts opts) throws JavaChainException {

            try {

                AlgorithmParameterSpec params = ECUtil.getECParameterSpec(Security.getProvider("SunEC")/*opts.getProvider()*/, keySize);
                if (params == null) {
                    throw new JavaChainException(
                            "No EC parameters available for key size "
                                    + keySize + " bits");
                }

                //create bigint
                int iValue =  new Random().nextInt(100);
                String strDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                long lBigInt = Long.parseLong(strDate + iValue);

                String publabel = String.format("BCPUB%s", Long.toHexString(lBigInt));
                String prvlabel = String.format("BCPRV%s", Long.toHexString(lBigInt));

                byte[] encodedParams =
                        ECUtil.encodeECParameterSpec(Security.getProvider("SunEC")/*opts.getProvider()*/, (ECParameterSpec)params);

                CK_ATTRIBUTE[] publicKeyTemplate = new CK_ATTRIBUTE[8];
                publicKeyTemplate[0] = new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_PUBLIC_KEY);
                publicKeyTemplate[1] = new CK_ATTRIBUTE(PKCS11Constants.CKA_KEY_TYPE, PKCS11Constants.CKK_EC);
                publicKeyTemplate[2] = new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, !ephemeral);
                publicKeyTemplate[3] = new CK_ATTRIBUTE(PKCS11Constants.CKA_VERIFY, true);

                publicKeyTemplate[4] = new CK_ATTRIBUTE(PKCS11Constants.CKA_PRIVATE, false);
                publicKeyTemplate[5] = new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, publabel);
                publicKeyTemplate[6] = new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, publabel);
                publicKeyTemplate[7] = new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_PARAMS, encodedParams);

                CK_ATTRIBUTE[] privateKeyTemplate = new CK_ATTRIBUTE[8];
                privateKeyTemplate[0] = new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_PRIVATE_KEY);
                privateKeyTemplate[1] = new CK_ATTRIBUTE(PKCS11Constants.CKA_KEY_TYPE, PKCS11Constants.CKK_EC);
                privateKeyTemplate[2] = new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, !ephemeral);
                privateKeyTemplate[3] = new CK_ATTRIBUTE(PKCS11Constants.CKA_SIGN, true);

                privateKeyTemplate[4] = new CK_ATTRIBUTE(PKCS11Constants.CKA_PRIVATE, true);
                privateKeyTemplate[5] = new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, prvlabel);
                privateKeyTemplate[6] = new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, prvlabel);
                privateKeyTemplate[7] = new CK_ATTRIBUTE(PKCS11Constants.CKA_EXTRACTABLE, !(opts.getNoImport()));


                long[] keyIDs = opts.getPKCS11().C_GenerateKeyPair
                        (opts.getSessionhandle(), new CK_MECHANISM(PKCS11Constants.CKM_EC_KEY_PAIR_GEN),
                                publicKeyTemplate, privateKeyTemplate);

                MECPoint ecpt = new MECPoint(opts.getPKCS11(),opts.getSessionhandle(),keyIDs[0]);

                // SKI
                MessageDigest shahashalg = null;
                try {
                    //计算公钥hash，再写回cka_id
                    shahashalg = MessageDigest.getInstance("SHA-256");
                    shahashalg.update(ecpt.getEcpt());
                    byte[] digestpub=shahashalg.digest();
                    byteSKI = digestpub;
                }catch (NoSuchAlgorithmException e) {
                    throw new JavaChainException("hash public key err!");
                }

                CK_ATTRIBUTE[] setski = new CK_ATTRIBUTE[2];
                setski[0] = new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, byteSKI);
                setski[1] = new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, MyByteToHex(byteSKI));
                opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keyIDs[0],setski);
                opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keyIDs[1],setski);

                publicKeyTemplate[6] = new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, MyByteToHex(byteSKI));
                publicKeyTemplate[7] = new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, byteSKI);
                privateKeyTemplate[6] = new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, MyByteToHex(byteSKI));
                privateKeyTemplate[7] = new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, byteSKI);


                PublicKey publicKey = PKCS11CspKey.publicKey
                        (opts, keyIDs[0], "EC", keySize, publicKeyTemplate);
                PrivateKey privateKey = PKCS11CspKey.privateKey
                        (opts, keyIDs[1], "EC", keySize, privateKeyTemplate);

                ECPublicKey ecdsapub = (ECPublicKey)publicKey;
                ikey = new EcdsaKeyOpts.EcdsaPriKey(byteSKI, (ECPrivateKey) privateKey,new EcdsaKeyOpts.EcdsaPubKey(byteSKI, ecdsapub));


            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                throw new JavaChainException("error");
            }
        }

        public IKey getIKey(){
            return ikey;
        }
    }


    // 导入ecdsa密钥
    public static class ImportECKey extends EcdsaImpl{

        /**
         * 导入ecdsa密钥
         *
         * @param prider        私钥DER编码
         * @param pubder        公钥DER编码
         * @param ephemeral     临时标记
         * @param opts          p11 factory
         * @param flagpubkey    公钥标记
         * @return 公钥摘要
         */
        public static byte[] importECKey(byte[] prider, byte[] pubder, boolean ephemeral, IPKCS11FactoryOpts opts, boolean flagpubkey, boolean impri)  throws JavaChainException{
            byte[] byteSKI;
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("EC", "SunEC");

                List<CK_ATTRIBUTE> keyTemplate = new ArrayList<CK_ATTRIBUTE>();

                if(flagpubkey == true) { //公钥
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubder);
                    ECPublicKey ecpubkey = (ECPublicKey)keyFactory.generatePublic(keySpec);
                    byte[] encodedParams =
                            ECUtil.encodeECParameterSpec(Security.getProvider("SunEC")/*opts.getProvider()*/, ecpubkey.getParams());

                    // SKI
                    MessageDigest shahashalg = null;

                    //计算公钥hash，再写回cka_id
                    shahashalg = MessageDigest.getInstance("SHA-256");
                    byte[] ecPt = ECUtil.encodePoint(ecpubkey.getW(), ecpubkey.getParams().getCurve());
                    byte[] tempecpt = data(ecPt);
                    shahashalg.update(tempecpt);
                    byte[] digestpub=shahashalg.digest();
                    byteSKI = digestpub;

                    if(!opts.getNoImport()) {
                        keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_PUBLIC_KEY));
                        keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_KEY_TYPE, PKCS11Constants.CKK_EC));
                        keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, !ephemeral));
                        keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_VERIFY, true));

                        keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_PRIVATE, false));
                        keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, MyByteToHex(byteSKI)));
                        keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, byteSKI));
                        keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_PARAMS, encodedParams));
                        keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_POINT, ecPt));
                    }
                }
                else
                {//私钥
                    byteSKI = importECKey(null,pubder,ephemeral,opts, true, true);
                    PKCS8EncodedKeySpec prikeySpec = new PKCS8EncodedKeySpec(prider);
                    ECPrivateKey ecprikey = (ECPrivateKey) keyFactory.generatePrivate(prikeySpec);
                    byte[] encodedParams =
                            ECUtil.encodeECParameterSpec(Security.getProvider("SunEC")/*opts.getProvider()*/, ecprikey.getParams());


                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_PRIVATE_KEY));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_KEY_TYPE, PKCS11Constants.CKK_EC));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, !ephemeral));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_SIGN, true));

                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_PRIVATE, true));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, MyByteToHex(byteSKI)));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, byteSKI));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_EXTRACTABLE, !(opts.getNoImport())));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_PARAMS, encodedParams));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_VERIFY, true));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKR_ATTRIBUTE_SENSITIVE, false));
                }

                if(!opts.getNoImport()) {
                    opts.getPKCS11().C_CreateObject(opts.getSessionhandle(), keyTemplate.toArray(new CK_ATTRIBUTE[0]));
                }

                return byteSKI;
            }catch(NoSuchAlgorithmException|NoSuchProviderException|InvalidKeySpecException |PKCS11Exception  e) {
                throw new JavaChainException("error!");
            }
        }

        /**
         * 获取IKey
         *
         * @param ski           公钥摘要
         * @param prider        私钥DER编码
         * @param pubder        公钥DER编码
         * @return IKey
         */
        public static IKey getKey(byte[] ski, byte[] pubder, byte[] prider) throws JavaChainException{
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("EC", "SunEC");
                X509EncodedKeySpec pubkeySpec = new X509EncodedKeySpec(pubder);
                ECPublicKey ecpubkey = (ECPublicKey)keyFactory.generatePublic(pubkeySpec);
                if(prider == null)
                {
                    return new EcdsaKeyOpts.EcdsaPriKey(ski,null,new EcdsaKeyOpts.EcdsaPubKey(ski, ecpubkey));
                }
                else {
                    PKCS8EncodedKeySpec prikeySpec = new PKCS8EncodedKeySpec(prider);
                    ECPrivateKey ecprikey = (ECPrivateKey) keyFactory.generatePrivate(prikeySpec);
                    return new EcdsaKeyOpts.EcdsaPriKey(ski, ecprikey, new EcdsaKeyOpts.EcdsaPubKey(ski, ecpubkey));
                }

            }catch(NoSuchAlgorithmException|NoSuchProviderException|InvalidKeySpecException e) {
                throw new JavaChainException("error!");
            }
        }
    }


    /**
     * 自定义ECpoint数据处理
     *
     * @param p11           PKCS
     * @param sessionhandle	session句柄
     * @param keyhandle 	公钥句柄
     * @return
     */
    public static class MECPoint{
        private byte[] ecpt;
        private byte[] oid;
        public MECPoint(PKCS11 p11, long sessionhandle, long keyhandle) {
            CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                    new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_POINT),
                    new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_PARAMS),
            };
            try {
                p11.C_GetAttributeValue(sessionhandle, keyhandle, attributes);

                byte[] tempoid = attributes[1].getByteArray();
                byte[] tempecpt = data(attributes[0].getByteArray());

                this.ecpt = tempecpt;
                this.oid = tempoid;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
            }
        }

        public byte[] getOID() {
            return oid;
        }

        public byte[] getEcpt() {
            return ecpt;
        }
    }




    /**
     * 数据处理
     *
     * @param tempecpt     ECPoint数据
     * @return 去tag后数据
     */
    public static byte[] data(byte[] tempecpt) {

        int len = tempecpt.length;
        byte[] tempdata = new byte[len];
        if(0 == (len % 2) &&
                (tempecpt[0] == (byte) 0x04)&&
                (tempecpt[len-1] == (byte) 0x04))
        {
            // Trim trailing 0x04
            System.arraycopy(tempecpt, 0, tempdata, 0, len-1);
        }
        else if((tempecpt[0] == (byte) 0x04) &&
                (tempecpt[2] == (byte) 0x04))
        {
            System.arraycopy(tempecpt, 2, tempdata, 0, len-2);
        }
        else
            tempdata = tempecpt;

        return tempdata;
    }


    /**
     * 进制转换
     *
     * @param b     二行制数据
     * @return  十六进制数据字符串
     */
    public static String MyByteToHex(byte[] b) //二行制转字符串
    {
        String hs="";
        String stmp="";
        for (int n=0;n<b.length;n++)
        {
            stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1) hs=hs+"0"+stmp;
            else hs=hs+stmp;
            if (n<b.length-1)  hs=hs+":";
        }
        return hs.toUpperCase();
    }
}
