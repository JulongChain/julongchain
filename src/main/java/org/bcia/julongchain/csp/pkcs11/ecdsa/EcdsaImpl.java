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

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.util.Convert;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.pkcs11.IPKCS11FactoryOpts;
import org.bcia.julongchain.csp.pkcs11.PKCS11CspLog;
import org.bcia.julongchain.csp.pkcs11.util.DataUtil;
import sun.security.ec.ECPublicKeyImpl;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.PKCS11Constants;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import sun.security.util.ECUtil;
import sun.security.x509.X509Key;

import java.io.IOException;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant.*;
import static sun.security.pkcs11.wrapper.PKCS11Constants.*;

/**
 * Class for Ecdsa Impl
 *
 * @author Ying Xu
 * @date 4/19/18
 * @company FEITIAN
 */

public class EcdsaImpl {
	
    // 生成ecdsa密钥
    public static class GenerateECKey extends EcdsaImpl{
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
        public void generateECKey(int keySize, boolean ephemeral, IPKCS11FactoryOpts opts) throws CspException {
            try {
            	
                ECParameterSpec params = ECUtil.getECParameterSpec(Security.getProvider("SunEC"), keySize);
                byte[] encodedParams = ECUtil.encodeECParameterSpec(Security.getProvider("SunEC"), params);                

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
                setski[1] = new CK_ATTRIBUTE(PKCS11Constants.CKA_LABEL, Convert.bytesToHexString(byteSKI));
                opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keypair[0],setski);
                opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keypair[1],setski);                
            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                EcdsaImpl.setLoggerErr(err, CLS_GENKEY);
                throw new CspException(err, ex.getCause());
            }catch(IOException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:IOException ErrMessage: %s", ex.getMessage());
                EcdsaImpl.setLoggerErr(err, CLS_GENKEY);
                throw new CspException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                EcdsaImpl.setLoggerErr(err, CLS_GENKEY);
                throw new CspException(err, ex.getCause());
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
        public static byte[] importECKey(byte[] prider, byte[] pubder, boolean ephemeral, IPKCS11FactoryOpts opts, boolean flagpubkey)  throws CspException{

            try {
                byte[] byteSKI;
                KeyFactory keyFactory = KeyFactory.getInstance("EC", "SunEC");
                List<CK_ATTRIBUTE> keyTemplate = new ArrayList<CK_ATTRIBUTE>();

                if(flagpubkey == true) {
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubder);
                    ECPublicKey ecpubkey = (ECPublicKey)keyFactory.generatePublic(keySpec);
                    byte[] encodedParams =
                            ECUtil.encodeECParameterSpec(Security.getProvider("SunEC"), ecpubkey.getParams());

                    // SKI
                    byte[] ecpointdata;
                    if (ecpubkey instanceof ECPublicKeyImpl) {
                        ecpointdata = ((ECPublicKeyImpl)ecpubkey).getEncodedPublicValue();
                    } else { 
                    	// instanceof ECPublicKey
                        ecpointdata = ECUtil.encodePoint(ecpubkey.getW(), ecpubkey.getParams().getCurve());
                    }

                    byte[] tempdata = new byte[ecpointdata.length-1];
                    System.arraycopy(ecpointdata, 1, tempdata, 0, ecpointdata.length-1);
                    ecpointvalue = tempdata;
                    //byte[] ecpointdata = ECUtil.encodePoint(ecpubkey.getW(), ecpubkey.getParams().getCurve());
                    byteSKI = getPublicHash(ecpointdata);

                    keyTemplate.add(new CK_ATTRIBUTE(CKA_CLASS, CKO_PUBLIC_KEY));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_EC));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_TOKEN, !ephemeral));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_VERIFY, true));

                    keyTemplate.add(new CK_ATTRIBUTE(CKA_PRIVATE, false));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_LABEL, Convert.bytesToHexString(byteSKI)));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_ID, byteSKI));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_EC_PARAMS, encodedParams));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_EC_POINT, tempdata));
                }
                else
                {
                    byteSKI = importECKey(null,pubder,ephemeral,opts, true);
                    PKCS8EncodedKeySpec prikeySpec = new PKCS8EncodedKeySpec(prider);
                    ECPrivateKey ecprikey = (ECPrivateKey) keyFactory.generatePrivate(prikeySpec);
                    byte[] encodedParams =
                            ECUtil.encodeECParameterSpec(Security.getProvider("SunEC"), ecprikey.getParams());

                    keyTemplate.add(new CK_ATTRIBUTE(CKA_CLASS, CKO_PRIVATE_KEY));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_EC));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_TOKEN, !ephemeral));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_SIGN, true));

                    keyTemplate.add(new CK_ATTRIBUTE(CKA_PRIVATE, true));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_LABEL, Convert.bytesToHexString(byteSKI)));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_ID, byteSKI));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_EXTRACTABLE, !(opts.getNoImport())));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_EC_PARAMS, encodedParams));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_VERIFY, true));
                    keyTemplate.add(new CK_ATTRIBUTE(CKR_ATTRIBUTE_SENSITIVE, false));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_VALUE, ecprikey.getS()));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_EC_POINT, ecpointvalue));
                }

                if(!opts.getNoImport()) {
                    opts.getPKCS11().C_CreateObject(opts.getSessionhandle(), keyTemplate.toArray(new CK_ATTRIBUTE[0]));                    
                }
                return byteSKI;

            }catch(NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
                EcdsaImpl.setLoggerErr(err, CLS_IMPORTKEY);
                throw new CspException(err, ex.getCause());
            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                EcdsaImpl.setLoggerErr(err, CLS_IMPORTKEY);
                throw new CspException(err, ex.getCause());
            }catch(InvalidKeySpecException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:InvalidKeySpecException ErrMessage: %s", ex.getMessage());
                EcdsaImpl.setLoggerErr(err, CLS_IMPORTKEY);
                throw new CspException(err, ex.getCause());
            }catch( NoSuchProviderException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:NoSuchProviderException ErrMessage: %s", ex.getMessage());
                EcdsaImpl.setLoggerErr(err, CLS_IMPORTKEY);
                throw new CspException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                EcdsaImpl.setLoggerErr(err, CLS_IMPORTKEY);
                throw new CspException(err, ex.getCause());
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
        public static IKey getKey(byte[] ski, byte[] pubder, byte[] prider) throws CspException{

            if(prider == null)
            {
                return new EcdsaKeyOpts.EcdsaPubKey(ski, pubder);
            }
            else {
                return new EcdsaKeyOpts.EcdsaPriKey(ski, prider, new EcdsaKeyOpts.EcdsaPubKey(ski, pubder));
            }

        }
    }

    /**
     * Derive Ecdas Keypair
     */
    public static class DeriveECKey{
    	private static byte[] byteSKI;
        private static byte[] pubder;
        
    	public DeriveECKey() {}

        /**
         *
         * @param ski           Identify for Search Key
         * @param ephemeral     Ephemeral(True/False)
         * @param flagprikey    Key type identification(PrivateKey: True  PublicKey: Flase)
         * @param opts          P11 factory
         * @return  IKey instance of EcdsaKeyOpts.EcdsaPubKey
         * @throws CspException
         */
    	public static IKey deriveKey(byte[] ski, boolean ephemeral, boolean flagprikey, IPKCS11FactoryOpts opts) throws CspException {
    		try {    		
    			List<CK_ATTRIBUTE> keyTemplate = new ArrayList<CK_ATTRIBUTE>();
    			if(!flagprikey)
    			{
    				long[] keypbu = findKeypairFromSKI(opts, false, ski);
    				if (keypbu==null || keypbu.length==0) {
                        String str=String.format("[JC_PKCS]:No Find Key");
                        EcdsaImpl.setLoggerErr(str, CLS_DERIV);
                        throw new CspException(str);
                    }
    				keyTemplate.add(new CK_ATTRIBUTE(CKA_CLASS, CKO_PUBLIC_KEY));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_EC));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_TOKEN, !ephemeral));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_VERIFY, true));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_PRIVATE, false));
    				
    				long keyhandle = opts.getPKCS11().C_DeriveKey(opts.getSessionhandle(), new CK_MECHANISM(CKM_DERIVEECCKEY),
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
                    setski[0] = new CK_ATTRIBUTE(CKA_ID, byteSKI);
                    setski[1] = new CK_ATTRIBUTE(CKA_LABEL, Convert.bytesToHexString(byteSKI));
                    opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),keyhandle,setski);                               
                    
                    return new EcdsaKeyOpts.EcdsaPubKey(byteSKI, pubder);
                    
    			} else {   				
    				
    				long[] keypri = findKeypairFromSKI(opts, true, ski);
    				if (keypri==null || keypri.length==0) {
                        String str=String.format("[JC_PKCS]:No Find Key");
                        EcdsaImpl.setLoggerErr(str, CLS_DERIV);
                        throw new CspException(str);
                    }
    				
    				keyTemplate.add(new CK_ATTRIBUTE(CKA_CLASS, CKO_PRIVATE_KEY));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_EC));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_TOKEN, !ephemeral));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_SIGN, true));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_PRIVATE, true));
                    //keyTemplate.add(new CK_ATTRIBUTE(CKA_LABEL, DataUtil.MyByteToHex(byteSKI)));
                    //keyTemplate.add(new CK_ATTRIBUTE(CKA_ID, byteSKI));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_EXTRACTABLE, !(opts.getNoImport())));
                    keyTemplate.add(new CK_ATTRIBUTE(CKA_VERIFY, true));
                    keyTemplate.add(new CK_ATTRIBUTE(CKR_ATTRIBUTE_SENSITIVE, false));
                    
                    long prikeyhandle = opts.getPKCS11().C_DeriveKey(opts.getSessionhandle(), new CK_MECHANISM(CKM_DERIVEECCKEY),
                    		keypri[0], keyTemplate.toArray(new CK_ATTRIBUTE[0]));
                    
                    CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                            new CK_ATTRIBUTE(CKA_EC_POINT),                            
                    };
                    opts.getPKCS11().C_GetAttributeValue(opts.getSessionhandle(),prikeyhandle, attributes);
                    
                    CK_ATTRIBUTE[] template = new CK_ATTRIBUTE[3];
                    template[0] = new CK_ATTRIBUTE(CKA_CLASS, CKO_PUBLIC_KEY);
                    template[1] = new CK_ATTRIBUTE(CKA_EC_POINT, attributes[0].getByteArray());
                    template[2] = new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_EC);
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
                    setski[0] = new CK_ATTRIBUTE(CKA_ID, byteSKI);
                    setski[1] = new CK_ATTRIBUTE(CKA_LABEL, Convert.bytesToHexString(byteSKI));
                    opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),pubkeyhandle[0],setski);                                
                    opts.getPKCS11().C_SetAttributeValue(opts.getSessionhandle(),prikeyhandle,setski);                                
                    
                    return new EcdsaKeyOpts.EcdsaPubKey(byteSKI, pubder);
    			}
    		
    		} catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                EcdsaImpl.setLoggerErr(err, CLS_DERIV);
                throw new CspException(err, ex.getCause());
            } catch(IOException ex) {
            	ex.printStackTrace();
                String err = String.format("[JC_PKCS]:IOException ErrMessage: %s", ex.getMessage());
                EcdsaImpl.setLoggerErr(err, CLS_DERIV);
                throw new CspException(err, ex.getCause());
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
            GetkeyEcKey.ski = ski;
            GetkeyEcKey.opts = opts;
        }

        public static IKey getkey() throws CspException{

            try {

                long[] keypbu = findKeypairFromSKI(opts, false, ski);
                if (keypbu==null || keypbu.length==0) {
                    String str = String.format("[JC_PKCS]:No Find Key");
                    EcdsaImpl.setLoggerInfo(str, CLS_GETKEY);
                    return null;
                }
                else
                {
                    CK_ATTRIBUTE[] publicKeyTemplate = new CK_ATTRIBUTE[] {
                            new CK_ATTRIBUTE(CKA_EC_POINT),
                            new CK_ATTRIBUTE(CKA_EC_PARAMS),
                    };

                    opts.getPKCS11().C_GetAttributeValue(opts.getSessionhandle(), keypbu[0], publicKeyTemplate);
                    ECParameterSpec params = ECUtil.getECParameterSpec(null, publicKeyTemplate[1].getByteArray());
                    byte[] pubder =  getPublicDer(publicKeyTemplate[0].getByteArray(), params);
                    // SKI
                    byte[] byteSKI = getPublicHash(publicKeyTemplate[0].getByteArray());
                    if(!DataUtil.compareByteArray(ski, byteSKI))
                    {
                    	String str = String.format("[JC_PKCS]:No Find Key");
                        EcdsaImpl.setLoggerInfo(str, CLS_GETKEY);
                    	return null;
                    }

                    return new EcdsaKeyOpts.EcdsaPubKey(ski, pubder);
                }
            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                EcdsaImpl.setLoggerErr(err, CLS_GETKEY);
                throw new CspException(err, ex.getCause());
            }catch(IOException ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:IOException ErrMessage: %s", ex.getMessage());
                EcdsaImpl.setLoggerErr(err, CLS_GETKEY);
                throw new CspException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                EcdsaImpl.setLoggerErr(err, CLS_GETKEY);
                throw new CspException(err, ex.getCause());
            }
        }

    }


    /**
     * Use the private key signature of the specified SKI
     *
     * @author Ying Xu
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
         * @param newMechanism  the mechanism
         * @param opts          p11factory
         */
        public static byte[] signECDSA(byte[] ski, byte[] digest, long newMechanism, IPKCS11FactoryOpts opts) throws CspException{

            try {
                long[] privatekey = findKeypairFromSKI(opts, true, ski);
                if (privatekey==null || privatekey.length==0)
                {
                    String str=String.format("[JC_PKCS]:No Find Key");
                    EcdsaImpl.setLoggerErr(str, CLS_SIGN);
                    throw new CspException(str);
                }
                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = newMechanism;
                opts.getPKCS11().C_SignInit(opts.getSessionhandle(), ckMechanism, privatekey[0]);
                byte[] signvalue = opts.getPKCS11().C_Sign(opts.getSessionhandle(), digest);
                return signvalue;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                EcdsaImpl.setLoggerErr(err, CLS_SIGN);
                throw new CspException(err, ex.getCause());
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                EcdsaImpl.setLoggerErr(err, CLS_SIGN);
                throw new CspException(err, ex.getCause());
            }

        }

    }


    /**
     * Use the public key verify signature of the specified SKI
     *
     * @author Ying Xu
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
        public boolean verifyECDSA(byte[] ski, byte[] signature, byte[] digest, long newMechanism, IPKCS11FactoryOpts opts) throws CspException{

            try {

                long[] publickey = findKeypairFromSKI(opts, false, ski);
                if (publickey==null || publickey.length==0) {
                    String str=String.format("[JC_PKCS]:No Find Key");
                    EcdsaImpl.setLoggerErr(str, CLS_VERIFY);
                    throw new CspException(str);
                }

                CK_MECHANISM ckMechanism = new CK_MECHANISM();
                ckMechanism.mechanism = newMechanism;
                opts.getPKCS11().C_VerifyInit(opts.getSessionhandle(), ckMechanism, publickey[0]);
                opts.getPKCS11().C_Verify(opts.getSessionhandle(), digest, signature);
                return true;

            }catch(PKCS11Exception ex) {
                ex.printStackTrace();
                String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
                EcdsaImpl.setLoggerErr(err, CLS_VERIFY);
                return false;
            }catch(Exception ex) {
            	ex.printStackTrace();
            	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
                EcdsaImpl.setLoggerErr(err, CLS_VERIFY);
                throw new CspException(err, ex.getCause());
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
    public static long[] findKeypairFromSKI(IPKCS11FactoryOpts opts, boolean bPri, byte[] ski) throws CspException{

        long keyclass = CKO_PUBLIC_KEY;
        if(bPri){
            keyclass = CKO_PRIVATE_KEY;
        }

        CK_ATTRIBUTE[] template = new CK_ATTRIBUTE[3];
        template[0] = new CK_ATTRIBUTE(CKA_CLASS, keyclass);
        template[1] = new CK_ATTRIBUTE(CKA_ID, ski);
        template[2] = new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_EC);

        try {
            opts.getPKCS11().C_FindObjectsInit(opts.getSessionhandle(), template);
            long[] keypair = opts.getPKCS11().C_FindObjects(opts.getSessionhandle(), 1);
            opts.getPKCS11().C_FindObjectsFinal(opts.getSessionhandle());
            return keypair;
        }catch(PKCS11Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
            setLoggerErr(err, CLS_SELF);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
        	ex.printStackTrace();
        	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, CLS_SELF);
            throw new CspException(err, ex.getCause());
        }
    }

    /**
     * Get Publickey DER Encoding of ASN.1 Types
     *
     */
    private static byte[] getPublicDer(byte[] attecpoint, ECParameterSpec params) throws CspException{
        try {
            ECPoint w = ECUtil.decodePoint(attecpoint, params.getCurve());
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            ECPublicKeySpec keySpec = new ECPublicKeySpec(w, params);
            X509Key key = (X509Key)keyFactory.generatePublic(keySpec);
            return key.getEncoded();
        }catch(IOException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:IOException ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, CLS_SELF);
            throw new CspException(err, ex.getCause());
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, CLS_SELF);
            throw new CspException(err, ex.getCause());
        }catch(InvalidKeySpecException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:InvalidKeySpecException ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, CLS_SELF);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
        	ex.printStackTrace();
        	String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, CLS_SELF);
            throw new CspException(err, ex.getCause());
        }
    }


    /**
     * get public key hash data
     *
     */
    private static byte[] getPublicHash(byte[] attecpoint) throws CspException {
        try {
            byte[] tempecpt = DataUtil.data(attecpoint);
            MessageDigest shahash = MessageDigest.getInstance("SHA-1");
            shahash.update(tempecpt);
            return shahash.digest();
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, CLS_SELF);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:Exception ErrMessage: %s", ex.getMessage());
            setLoggerErr(err, CLS_SELF);
            throw new CspException(err, ex.getCause());
        }
    }
    

	
    
    private static void setLoggerDebug(String msg, int classNO){

        PKCS11CspLog csplog = new PKCS11CspLog();
        switch (classNO){
            case CLS_GENKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, EcdsaImpl.GenerateECKey.class);
                break;
            case CLS_IMPORTKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, EcdsaImpl.ImportECKey.class);
                break;
            case CLS_GETKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, EcdsaImpl.GetkeyEcKey.class);
                break;
            case CLS_SIGN:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, EcdsaImpl.SignECKey.class);
                break;
            case CLS_VERIFY:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, EcdsaImpl.VerifyECKey.class);
                break;
            case CLS_DERIV:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, EcdsaImpl.DeriveECKey.class);
                break;
            default:
                csplog.setLogMsg(msg, csplog.LEVEL_DEBUG, EcdsaImpl.class);
                break;
        }
    }

    private static void setLoggerInfo(String msg, int classNO){
        PKCS11CspLog csplog = new PKCS11CspLog();
        switch (classNO){
            case CLS_GENKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, EcdsaImpl.GenerateECKey.class);
                break;
            case CLS_IMPORTKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, EcdsaImpl.ImportECKey.class);
                break;
            case CLS_GETKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, EcdsaImpl.GetkeyEcKey.class);
                break;
            case CLS_SIGN:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, EcdsaImpl.SignECKey.class);
                break;
            case CLS_VERIFY:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, EcdsaImpl.VerifyECKey.class);
                break;
            case CLS_DERIV:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, EcdsaImpl.DeriveECKey.class);
                break;
            default:
                csplog.setLogMsg(msg, csplog.LEVEL_INFO, EcdsaImpl.class);
                break;
        }
    }

    private static void setLoggerErr(String msg, int classNO){
        PKCS11CspLog csplog = new PKCS11CspLog();
        switch (classNO){
            case CLS_GENKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, EcdsaImpl.GenerateECKey.class);
                break;
            case CLS_IMPORTKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, EcdsaImpl.ImportECKey.class);
                break;
            case CLS_GETKEY:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, EcdsaImpl.GetkeyEcKey.class);
                break;
            case CLS_SIGN:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, EcdsaImpl.SignECKey.class);
                break;
            case CLS_VERIFY:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, EcdsaImpl.VerifyECKey.class);
                break;
            case CLS_DERIV:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, EcdsaImpl.DeriveECKey.class);
                break;
            default:
                csplog.setLogMsg(msg, csplog.LEVEL_ERROR, EcdsaImpl.class);
                break;
        }
    }


}
