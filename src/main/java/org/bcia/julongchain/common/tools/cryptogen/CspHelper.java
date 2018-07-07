/**
 * Copyright BCIA. All Rights Reserved.
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
package org.bcia.julongchain.common.tools.cryptogen;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.tools.cryptogen.sm2cert.SM2PublicKeyImpl;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2KeyGenOpts;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2KeyImportOpts;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2PublicKey;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenhao, yegangcheng
 * @date 2018/4/3
 * @company Excelsecu
 */
public class CspHelper {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CspHelper.class);
    private static final ICsp csp = getCsp();

    static {
        try {
            GlobalMspManagement.initLocalMsp();
        } catch (MspException e) {
            log.error(e.getMessage(), e);
        }

//        List<IFactoryOpts> list = new ArrayList<>();
//        IFactoryOpts opts = new GmFactoryOpts() {
//            @Override
//            public boolean isDefaultCsp() {
//                return true;
//            }
//        };
//        try {
//            MspConfig mspConfig = MspConfigFactory.loadMspConfig();
//            GmFactoryOpts factoryOpts=new GmFactoryOpts();
//            factoryOpts.parseFrom(mspConfig.getNode().getCsp().getFactoryOpts().get("gm"));
//            list.add(factoryOpts);
//        } catch (FileNotFoundException e) {
//            log.error(e.getMessage());
//        }
//        list.add(opts);
//        CspManager.initCspFactories(list);
    }

    public static ICsp getCsp() {
        return CspManager.getDefaultCsp();
    }

    public static IKey loadPrivateKey(String keystorePath) throws JavaChainException {
        File keyStoreDir = new File(keystorePath);
        File[] files = keyStoreDir.listFiles();
        if (!keyStoreDir.isDirectory() || files == null) {
            log.error("invalid directory for keystorePath " + keystorePath);
            return null;
        }
        for (File file : files) {
            if (!file.getName().endsWith("_sk")) {
                continue;
            }
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                PemReader pemReader = new PemReader(reader);
                PemObject pemObject = pemReader.readPemObject();
                reader.close();

                byte[] encodedData = pemObject.getContent();
                List<Object> list = decodePrivateKeyPKCS8(encodedData);
                Object rawKey = list.get(1);
                return csp.keyImport(rawKey, new SM2KeyImportOpts(true));
            } catch (Exception e) {
                log.error("An error occurred on loadPrivateKey: {}", e.getMessage());
            }
        }
        throw new JavaChainException("no pem file found");
    }

    private static byte[] encodePrivateKeyPKCS8(byte[] privateKey, AlgorithmId algId) throws JavaChainException {
        DerOutputStream encodedPriKey = new DerOutputStream();
        DerOutputStream var = new DerOutputStream();
        try {
            var.putInteger(BigInteger.ZERO);
            algId.encode(var);
            var.putOctetString(privateKey);
            encodedPriKey.write((byte) 48, var);
            return encodedPriKey.toByteArray();
        } catch (IOException e) {
            throw new JavaChainException("An error occurred :" + e);
        }
    }


    private static List<Object> decodePrivateKeyPKCS8(byte[] encodedData) throws JavaChainException {
        try {
            DerValue derValue = new DerValue(new ByteArrayInputStream(encodedData));
            if (derValue.tag != 48) {
                throw new JavaChainException("invalid key format");
            } else {
                BigInteger version = derValue.data.getBigInteger();
                if (!version.equals(BigInteger.ZERO)) {
                    throw new JavaChainException("version mismatch: (supported: " + Debug.toHexString(BigInteger.ZERO) + ", parsed: " + Debug.toHexString(version));
                } else {
                    AlgorithmId algId = AlgorithmId.parse(derValue.data.getDerValue());
                    byte[] rawPrivateKey = derValue.data.getOctetString();
                    List<Object> list = new ArrayList<>();
                    list.add(algId);
                    list.add(rawPrivateKey);
                    return list;
                }
            }
        } catch (IOException e) {
            throw new JavaChainException("IOException : " + e.getMessage());
        }
    }


    public static IKey generatePrivateKey(String keystorePath) throws JavaChainException {

        try {
            IKey priv = csp.keyGen(new SM2KeyGenOpts() {
                @Override
                public boolean isEphemeral() {
                    return true;
                }
            });
            byte[] encodedData = encodePrivateKeyPKCS8(priv.toBytes(), new AlgorithmId(SM2PublicKeyImpl.SM2_OID));
            String path = Paths.get(keystorePath, Hex.toHexString(priv.ski()) + "_sk").toString();
            Util.pemExport(path, "PRIVATE KEY", encodedData);
            return priv;
        } catch (Exception e) {
            throw new JavaChainException("An error occurred" + e);
        }
    }

    public static ECPublicKey getSM2PublicKey(IKey priv) throws JavaChainException {
        IKey pubKey;
        try {
            pubKey = priv.getPublicKey();
        } catch (Exception e) {
            priv.toBytes();
            pubKey = priv.getPublicKey();
        }
        if (!(pubKey instanceof SM2PublicKey)) {
            throw new JavaChainException("pubKey is not the instance of SM2Key method");
        }
        SM2PublicKey sm2PublicKey = (SM2PublicKey) pubKey;
        try {
            byte[] bytes = sm2PublicKey.toBytes();
            // 默认非压缩公钥
            if (bytes[0] != 0x04) {
                throw new JavaChainException("CspHelper getSM2PublicKey publicKey not uncompressed");
            }

            int xLength = (bytes.length - 1) / 2;
            byte[] bytesX = new byte[xLength];
            byte[] bytesY = new byte[xLength];
            System.arraycopy(bytes, 1, bytesX, 0, xLength);
            System.arraycopy(bytes, 1 + xLength, bytesY, 0, xLength);

            ECPoint secECPoint = new ECPoint(new BigInteger(bytesX), new BigInteger(bytesY));
            return new SM2PublicKeyImpl(secECPoint);
        } catch (Exception e) {
            throw new JavaChainException("an error occurred on getECPublicKey: " + e.getMessage());
        }
    }
}
