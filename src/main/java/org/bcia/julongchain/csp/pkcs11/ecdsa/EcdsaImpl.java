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
package org.bcia.julongchain.csp.pkcs11.ecdsa;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.pkcs11.IPKCS11FactoryOpts;
import org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant;
import org.bcia.julongchain.csp.pkcs11.util.DataUtil;
import sun.security.ec.ECPublicKeyImpl;
import sun.security.pkcs11.wrapper.*;
import sun.security.util.ECUtil;
import sun.security.x509.X509Key;

import java.io.IOException;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import java.text.SimpleDateFormat;

import static sun.security.pkcs11.wrapper.PKCS11Constants.*;

/**
 * Class description
 *
 * @author XuYing
 * @date 4/19/18
 * @company FEITIAN
 */

//Ecdsa Impl
public class EcdsaImpl {
	private static JavaChainLog logger;
    // 生成ecdsa密钥
    public static class generateECKey extends EcdsaImpl{
        private byte[] byteSKI;
        private byte[] pubder;

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
            	
                ECParameterSpec params = ECUtil.getECParameterSpec(Security.getProvider("SunEC"), keySize);
                byte[] encodedParams = ECUtil.encodeECParameterSpec(Security.getProvider("SunEC"), params);
                //ECParameterSpec params1 = ECUtil.getECParameterSpec(Security.getProvider("SunEC"), encodedParams);

                //create bigint
                int iValue =  new Random().nextInt(100);
                String strDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                long lBigInt = Long.parseLong(strDate + iValue);
                String publabel = String.format("BCPUB%s", Long.toHexString(lBigInt));
                String prvlabel = String.format("BCPRV%s", Long.toHexString(lBigInt));

                CK_ATTRIBUTE[] publicKeyTemplate = new CK_ATTRIBUTE[8];
                publicKeyTemplate[0] = new CK_ATTRIBUTE(CKA_CLASS, CKO_PUBLIC_KEY);
                publicKeyTemplate[1] = new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_EC);
                publicKeyTemplate[2] = new CK_ATTRIBUTE(CKA_TOKEN, !ephemeral);
                publicKeyTemplate[3] = new CK_ATTRIBUTE(CKA_VERIFY, true);

                publicKeyTemplate[4] = new CK_ATTRIBUTE(CKA_PRIVATE, false);
                publicKeyTemplate[5] = new CK_ATTRIBUTE(CKA_LABEL, publabel);
                publicKeyTemplate[6] = new CK_ATTRIBUTE(CKA_ID, publabel);
                publicKeyTemplate[7] = new CK_ATTRIBUTE(CKA_EC_PARAMS, encodedParams);

                CK_ATTRIBUTE[] privateKeyTemplate = new CK_ATTRIBUTE[8];
                privateKeyTemplate[0] = new CK_ATTRIBUTE(CKA_CLASS, CKO_PRIVATE_KEY);
                privateKeyTemplate[1] = new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_EC);
                privateKeyTemplate[2] = new CK_ATTRIBUTE(CKA_TOKEN, !ephemeral);
                privateKeyTemplate[3] = new CK_ATTRIBUTE(CKA_SIGN, true);

                privateKeyTemplate[4] = new CK_ATTRIBUTE(CKA_PRIVATE, true);
                //privateKeyTemplate[5] = new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, prvlabel);
                privateKeyTemplate[5] = new CK_ATTRIBUTE(CKA_SENSITIVE, opts.getNoImport());
                privateKeyTemplate[6] = new CK_ATTRIBUTE(CKA_ID, prvlabel);
                privateKeyTemplate[7] = new CK_ATTRIBUTE(CKA_EXTRACTABLE, !(opts.getNoImport()));


                long[] keypair = opts.getPKCS11().C_GenerateKeyPair
                        (opts.getSessionhandle(), new CK_MECHANISM(PKCS11Constants.CKM_EC_KEY_PAIR_GEN),
                                publicKeyTemplate, privateKeyTemplate);

                CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                        new CK_ATTRIBUTE(CKA_EC_POINT),
                        new CK_ATTRIBUTE(CKA_EC_PARAMS),
                };
                opts.getPKCS11().C_GetAttributeValue(opts.getSessionhandle(),keypair[0],
                        attributes);


                ECParameterSpec params2 = ECUtil.getECParameterSpec(Security.getProvider("SunEC"), attributes[1].getByteArray());

                pubder =  getPublicDer(attributes[0].getByteArray(), params2);
                // SKI
                byteSKI = getPublicHash(attributes[0].getByteArray());


                CK_ATTRIBUTE[] setski = new CK_ATTRIBUTE[2];
                setski[0] = new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, byteSKI);
                setski[1] = new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, DataUtil.MyByteToHex(byteSKI));
                opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keypair[0],setski);
                opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keypair[1],setski);                
            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(IOException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:IOException ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }

        }

        public IKey getIKey(){
            return new EcdsaKeyOpts.EcdsaPubKey(byteSKI, pubder);
        }
    }


    // 导入ecdsa密钥
    public static class ImportECKey extends EcdsaImpl{

    	private static byte[] ecpointvalue;
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
        public static byte[] importECKey(byte[] prider, byte[] pubder, boolean ephemeral, IPKCS11FactoryOpts opts, boolean flagpubkey)  throws JavaChainException{

            try {
                byte[] byteSKI;
                KeyFactory keyFactory = KeyFactory.getInstance("EC", "SunEC");
                List<CK_ATTRIBUTE> keyTemplate = new ArrayList<CK_ATTRIBUTE>();

                if(flagpubkey == true) {
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubder);
                    ECPublicKey ecpubkey = (ECPublicKey)keyFactory.generatePublic(keySpec);
                    byte[] encodedParams =
                            ECUtil.encodeECParameterSpec(Security.getProvider("SunEC")/*null*/, ecpubkey.getParams());

                    // SKI
                    byte[] ecpointdata;
                    if (ecpubkey instanceof ECPublicKeyImpl) {
                        ecpointdata = ((ECPublicKeyImpl)ecpubkey).getEncodedPublicValue();
                    } else { // instanceof ECPublicKey
                        ecpointdata = ECUtil.encodePoint(ecpubkey.getW(), ecpubkey.getParams().getCurve());
                    }

                    byte[] tempdata = new byte[ecpointdata.length-1];
                    System.arraycopy(ecpointdata, 1, tempdata, 0, ecpointdata.length-1);
                    ecpointvalue = tempdata;
                    //byte[] ecpointdata = ECUtil.encodePoint(ecpubkey.getW(), ecpubkey.getParams().getCurve());
                    byteSKI = getPublicHash(ecpointdata);

                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_PUBLIC_KEY));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_KEY_TYPE, PKCS11Constants.CKK_EC));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, !ephemeral));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_VERIFY, true));

                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_PRIVATE, false));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, DataUtil.MyByteToHex(byteSKI)));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, byteSKI));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_PARAMS, encodedParams));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_POINT, tempdata));
                }
                else
                {
                    byteSKI = importECKey(null,pubder,ephemeral,opts, true);
                    PKCS8EncodedKeySpec prikeySpec = new PKCS8EncodedKeySpec(prider);
                    ECPrivateKey ecprikey = (ECPrivateKey) keyFactory.generatePrivate(prikeySpec);
                    byte[] encodedParams =
                            ECUtil.encodeECParameterSpec(Security.getProvider("SunEC")/*null*/, ecprikey.getParams());

                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_PRIVATE_KEY));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_KEY_TYPE, PKCS11Constants.CKK_EC));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, !ephemeral));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_SIGN, true));

                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_PRIVATE, true));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, DataUtil.MyByteToHex(byteSKI)));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, byteSKI));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_EXTRACTABLE, !(opts.getNoImport())));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_PARAMS, encodedParams));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_VERIFY, true));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKR_ATTRIBUTE_SENSITIVE, false));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_VALUE, ecprikey.getS()));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_POINT, ecpointvalue));
                }

                if(!opts.getNoImport()) {
                    long test = opts.getPKCS11().C_CreateObject(opts.getSessionhandle(), keyTemplate.toArray(new CK_ATTRIBUTE[0]));                    
                }
                return byteSKI;

            }catch(NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(InvalidKeySpecException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:InvalidKeySpecException ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch( NoSuchProviderException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:NoSuchProviderException ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
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

            if(prider == null)
            {
                return new EcdsaKeyOpts.EcdsaPubKey(ski, pubder);
            }
            else {
                return new EcdsaKeyOpts.EcdsaPriKey(ski, prider, new EcdsaKeyOpts.EcdsaPubKey(ski, pubder));
            }

        }
    }
    
    
    public static class DeriveECKey{
    	private static byte[] byteSKI;
        private static byte[] pubder;
        
    	public DeriveECKey() {}
    	
    	public static IKey deriveKey(byte[] ski, boolean ephemeral, boolean flagprikey, IPKCS11FactoryOpts opts) throws JavaChainException {
    		try {    		
    			List<CK_ATTRIBUTE> keyTemplate = new ArrayList<CK_ATTRIBUTE>();
    			if(!flagprikey)
    			{
    				long[] keypbu = findKeypairFromSKI(opts, false, ski);
    				if (keypbu==null || (keypbu!=null && keypbu.length==0)) {
                        String str=null;
                        str=String.format("[JC_PKCS]:No Find Key");
                        logger.error(str);
                        return null;
                    }
    				keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_PUBLIC_KEY));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_KEY_TYPE, PKCS11Constants.CKK_EC));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, !ephemeral));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_VERIFY, true));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_PRIVATE, false));                    
    				
    				long keyhandle = opts.getPKCS11().C_DeriveKey(opts.getSessionhandle(), new CK_MECHANISM(PKCS11CSPConstant.CKM_DERIVEECCKEY),
    						keypbu[0], keyTemplate.toArray(new CK_ATTRIBUTE[0]));
    				
    				CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                            new CK_ATTRIBUTE(CKA_EC_POINT),
                            new CK_ATTRIBUTE(CKA_EC_PARAMS),
                    };
                    opts.getPKCS11().C_GetAttributeValue(opts.getSessionhandle(),keyhandle, attributes);

                    ECParameterSpec params = ECUtil.getECParameterSpec(Security.getProvider("SunEC"), attributes[1].getByteArray());
                    pubder =  getPublicDer(attributes[0].getByteArray(), params);
                    // SKI
                    byteSKI = getPublicHash(attributes[0].getByteArray());

                    CK_ATTRIBUTE[] setski = new CK_ATTRIBUTE[2];
                    setski[0] = new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, byteSKI);
                    setski[1] = new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, DataUtil.MyByteToHex(byteSKI));
                    opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keyhandle,setski);                               
                    
                    return new EcdsaKeyOpts.EcdsaPubKey(byteSKI, pubder);
                    
    			} else {   				
    				
    				long[] keypri = findKeypairFromSKI(opts, true, ski);
    				if (keypri==null || (keypri!=null && keypri.length==0)) {
                        String str=null;
                        str=String.format("[JC_PKCS]:No Find Key");
                        logger.error(str);
                        return null;
                    }
    				
    				keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_PRIVATE_KEY));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_KEY_TYPE, PKCS11Constants.CKK_EC));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, !ephemeral));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_SIGN, true));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_PRIVATE, true));
                    //keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, DataUtil.MyByteToHex(byteSKI)));
                    //keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, byteSKI));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_EXTRACTABLE, !(opts.getNoImport())));                 
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKA_VERIFY, true));
                    keyTemplate.add(new CK_ATTRIBUTE(PKCS11Constants.CKR_ATTRIBUTE_SENSITIVE, false));
                    
                    long prikeyhandle = opts.getPKCS11().C_DeriveKey(opts.getSessionhandle(), new CK_MECHANISM(PKCS11CSPConstant.CKM_DERIVEECCKEY),
                    		keypri[0], keyTemplate.toArray(new CK_ATTRIBUTE[0]));
                    
                    CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                            new CK_ATTRIBUTE(CKA_EC_POINT),                            
                    };
                    opts.getPKCS11().C_GetAttributeValue(opts.getSessionhandle(),prikeyhandle, attributes);
                    
                    CK_ATTRIBUTE[] template = new CK_ATTRIBUTE[3];
                    template[0] = new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_PUBLIC_KEY);
                    template[1] = new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_POINT, attributes[0].getByteArray());
                    template[2] = new CK_ATTRIBUTE(PKCS11Constants.CKA_KEY_TYPE, PKCS11Constants.CKK_EC);
                    opts.getPKCS11().C_FindObjectsInit(opts.getSessionhandle(), template);
                    long[] pubkeyhandle = opts.getPKCS11().C_FindObjects(opts.getSessionhandle(), 1);
                    opts.getPKCS11().C_FindObjectsFinal(opts.getSessionhandle());
                    
                    CK_ATTRIBUTE[] attributes1 = new CK_ATTRIBUTE[] {
                            new CK_ATTRIBUTE(CKA_EC_POINT),
                            new CK_ATTRIBUTE(CKA_EC_PARAMS),
                    };
                    opts.getPKCS11().C_GetAttributeValue(opts.getSessionhandle(),pubkeyhandle[0], attributes1);
                    ECParameterSpec params = ECUtil.getECParameterSpec(Security.getProvider("SunEC"), attributes1[1].getByteArray());
                    pubder =  getPublicDer(attributes1[0].getByteArray(), params);
                    // SKI
                    byteSKI = getPublicHash(attributes1[0].getByteArray());

                    CK_ATTRIBUTE[] setski = new CK_ATTRIBUTE[2];
                    setski[0] = new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, byteSKI);
                    setski[1] = new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, DataUtil.MyByteToHex(byteSKI));
                    opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),pubkeyhandle[0],setski);                                
                    opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),prikeyhandle,setski);                                
                    
                    return new EcdsaKeyOpts.EcdsaPubKey(byteSKI, pubder);
    			}
    		
    		} catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            } catch(IOException ex) {
            	ex.printStackTrace();
                String err = String.format("[JC_PKCS]:IOException ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }
    	}
    }


    /**
     * Get Ecdsa PublicKey
     *
     * @author xuying
     * @date 2018/05/20
     * @company FEITIAN
     */
    public static final class GetkeyEcKey{

        private static byte[] ski;
        private static IPKCS11FactoryOpts opts;

        public GetkeyEcKey(byte[] ski, IPKCS11FactoryOpts opts) {
            this.ski = ski;
            this.opts = opts;
        }

        public static IKey getkey() throws JavaChainException{

            try {

                long[] keypbu = findKeypairFromSKI(opts, false, ski);
                if (keypbu==null || (keypbu!=null && keypbu.length==0)) {
                    String str=null;
                    str=String.format("[JC_PKCS]:No Find Key");
                    logger.error(str);
                    return null;
                }
                else
                {
                    CK_ATTRIBUTE[] publicKeyTemplate = new CK_ATTRIBUTE[] {
                            new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_POINT),
                            new CK_ATTRIBUTE(PKCS11Constants.CKA_EC_PARAMS),
                    };

                    opts.getPKCS11().C_GetAttributeValue(opts.getSessionhandle(), keypbu[0], publicKeyTemplate);
                    ECParameterSpec params = ECUtil.getECParameterSpec(null, publicKeyTemplate[1].getByteArray());
                    byte[] pubder =  getPublicDer(publicKeyTemplate[0].getByteArray(), params);
                    // SKI
                    byte[] byteSKI = getPublicHash(publicKeyTemplate[0].getByteArray());


                    return new EcdsaKeyOpts.EcdsaPubKey(ski, pubder);
                }
            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(IOException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:IOException ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }
        }

    }


    /**
     * Use the private key signature of the specified SKI
     *
     * @author xuying
     * @date 2018/05/20
     * @company FEITIAN
     */
    public static class SignECKey{

        public SignECKey() {

        }


        /**
         * sign data
         *
         * @param ski           cka_id value
         * @param digest        digest data
         * @param newMechanism  the alg
         * @param opts          p11factory
         */
        public static byte[] signECDSA(byte[] ski, byte[] digest, long newMechanism, IPKCS11FactoryOpts opts) throws JavaChainException{

            try {
                long[] privatekey = findKeypairFromSKI(opts, true, ski);
                if (privatekey==null || (privatekey!=null && privatekey.length==0))
                {
                    logger.error("[JC_PKCS]:No Find Key");
                    return null;
                }
                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = newMechanism;
                opts.getPKCS11().C_SignInit(opts.getSessionhandle(), ckMechanism, privatekey[0]);
                byte[] signvalue = opts.getPKCS11().C_Sign(opts.getSessionhandle(), digest);
                return signvalue;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }

        }

    }


    /**
     * Use the public key verify signature of the specified SKI
     *
     * @author xuying
     * @date 2018/05/20
     * @company FEITIAN
     */
    public static class VerifyECKey{
        public VerifyECKey() {

        }


        /**
         * verify signature
         *
         * @param ski           cka_id value
         * @param signature     signature data
         * @param digest        digest data
         * @param newMechanism  the alg
         * @param opts          p11factory
         */
        public boolean verifyECDSA(byte[] ski, byte[] signature, byte[] digest, long newMechanism, IPKCS11FactoryOpts opts) throws JavaChainException{

            try {

                long[] publickey = findKeypairFromSKI(opts, false, ski);
                if (publickey==null || (publickey!=null && publickey.length==0)) {
                    logger.error("[JC_PKCS]:No Find Key!");
                }

                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = newMechanism;
                opts.getPKCS11().C_VerifyInit(opts.getSessionhandle(), ckMechanism, publickey[0]);
                opts.getPKCS11().C_Verify(opts.getSessionhandle(), digest, signature);
                return true;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                logger.error(err);
                return false;
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                logger.error(err);
                throw new JavaChainException(err, ex.getCause());
            }
        }
    }


    /**
     * Find the key of the specified SKI
     *
     * @param opts      p11factory
     * @param bPri      private flag
     * @param ski       cka_id value
     */
    public static long[] findKeypairFromSKI(IPKCS11FactoryOpts opts, boolean bPri, byte[] ski) throws JavaChainException{

        long keyclass = PKCS11Constants.CKO_PUBLIC_KEY;
        if(bPri)
            keyclass = PKCS11Constants.CKO_PRIVATE_KEY;

        CK_ATTRIBUTE[] template = new CK_ATTRIBUTE[3];
        template[0] = new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, keyclass);
        template[1] = new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, ski);
        template[2] = new CK_ATTRIBUTE(PKCS11Constants.CKA_KEY_TYPE, PKCS11Constants.CKK_EC);

        try {
            opts.getPKCS11().C_FindObjectsInit(opts.getSessionhandle(), template);
            long[] keypair = opts.getPKCS11().C_FindObjects(opts.getSessionhandle(), 1);
            opts.getPKCS11().C_FindObjectsFinal(opts.getSessionhandle());
            return keypair;
        }catch(PKCS11Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
        }catch(Exception ex) {
        	ex.printStackTrace();
        	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
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
     * get public key der code
     *
     */
    private static byte[] getPublicDer(byte[] attecpoint, ECParameterSpec params) throws JavaChainException{
        try {
            ECPoint w = ECUtil.decodePoint(attecpoint, params.getCurve());
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            ECPublicKeySpec keySpec = new ECPublicKeySpec(w, params);
            X509Key key = (X509Key)keyFactory.generatePublic(keySpec);
            return key.getEncoded();
        }catch(IOException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:IOException ErrMessage: %s", ex.getMessage());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
        }catch(InvalidKeySpecException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:InvalidKeySpecException ErrMessage: %s", ex.getMessage());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
        }catch(Exception ex) {
        	ex.printStackTrace();
        	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
        }
    }


    /**
     * get public key hash data
     *
     */
    private static byte[] getPublicHash(byte[] attecpoint) throws JavaChainException {
        try {
            byte[] tempecpt = data(attecpoint);
            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            shahash.update(tempecpt);
            return shahash.digest();
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
        }
    }


}
