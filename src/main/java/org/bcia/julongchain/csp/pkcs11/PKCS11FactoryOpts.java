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
package org.bcia.julongchain.csp.pkcs11;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.csp.pkcs11.entity.PKCS11Config;
import org.bcia.julongchain.csp.pkcs11.entity.PKCS11Lib;
import org.bcia.julongchain.csp.pkcs11.sw.IPKCS11SwFactoryOpts;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Constants;
import sun.security.pkcs11.wrapper.PKCS11Exception;


import java.io.IOException;
import java.util.Map;

/**
 * Class description
 *
 * @author
 * @date 4/19/18
 * @company FEITIAN
 */
public class PKCS11FactoryOpts implements IPKCS11FactoryOpts, IPKCS11SwFactoryOpts {

    private boolean bSensitive;
    private boolean bSoftVerify;
    private boolean bUseEcX963Encodeing;

    private long sessionhandle;
    private PKCS11 p11;
    private long slot;
    private char[] pin;

    private String path;


    PKCS11CspLog csplog = new PKCS11CspLog();


    public PKCS11FactoryOpts(PKCS11Config pkcsConf) {
        this.bSoftVerify = pkcsConf.getSoftVerify();
        this.bSensitive = pkcsConf.getnoKImport();
        this.path = pkcsConf.getPath();
    }

    public PKCS11FactoryOpts(PKCS11Lib pkcslib, PKCS11Config pkcsConf) throws JavaChainException {

        this.bSoftVerify = pkcsConf.getSoftVerify();
        this.bSensitive = pkcsConf.getnoKImport();

        boolean bfind = false;
        try {

            p11 = PKCS11.getInstance(pkcslib.getLibrary(), "C_GetFunctionList", null, false);
            long[] slots = p11.C_GetSlotList(true);
            int i=0;
            for(i=0;i<slots.length;i++)
            {

                char[] chars = p11.C_GetTokenInfo(slots[i]).label;
                String label = new String(new String(chars).getBytes("iso-8859-1"), "utf-8");
                String sn = new String(p11.C_GetTokenInfo(slots[i]).serialNumber);

                if(pkcslib.getKeyLabel().equals(label.trim()) && pkcslib.getKeySN().equals(sn.trim()))
                {
                    bfind = true;
                    slot = slots[i];
                    break;
                }
            }

            if(!bfind)
            {
                String str=null;
                str=String.format("[JC_PKCS]:Could not find token with label %s and serialNumber %s", pkcslib.getKeyLabel(),pkcslib.getKeySN());
                csplog.setLogMsg(str, 2, PKCS11FactoryOpts.class);
                throw new JavaChainException(str);
            }

            this.sessionhandle = p11.C_OpenSession(slot,
                    PKCS11Constants.CKF_SERIAL_SESSION|PKCS11Constants.CKF_RW_SESSION, null, null);
            pin = pkcslib.getKeyPin().toCharArray();
            p11.C_Login(sessionhandle, PKCS11Constants.CKU_USER, pin);

        }catch (IOException ex){
            String err = String.format("[JC_PKCS]:IOException ErrMessage:", ex.getMessage());
            csplog.setLogMsg(err, 2, PKCS11FactoryOpts.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(PKCS11Exception ex) {
            if(ex.getErrorCode() == PKCS11Constants.CKR_USER_ALREADY_LOGGED_IN)
            {
                csplog.setLogMsg("Already loggin!", 0, PKCS11FactoryOpts.class);
            }else
            {
                String err = String.format("[JC_PKCS]:PKCS11Exception code: 0x%08x", ex.getErrorCode());
                csplog.setLogMsg(err, 2, PKCS11FactoryOpts.class);
                throw new JavaChainException(err, ex.getCause());
            }
        }
    }



    @Override
    public void optFinalized() throws JavaChainException{
        try {
            p11.C_CloseSession(sessionhandle);
        }
        catch (PKCS11Exception ex){
            String err = String.format("[JC_PKCS]:PKCS11Exception code: 0x%08x", ex.getErrorCode());
            throw new JavaChainException(err, ex.getCause());
        }
    }

    @Override
    public String getProviderName() {
        return PROVIDER_PKCS11;
    }

    @Override
    public String getProviderDescription() {
        return null;
    }

    @Override
    public String getKeyStore() {
        return null;
    }

    @Override
    public void parseFrom(Map<String, String> optMap) {

    }

    @Override
    public long getSessionhandle() {
        return sessionhandle;
    }

    @Override
    public PKCS11 getPKCS11() {
        return p11;
    }

    @Override
    public boolean getSoftVerify() {
        return bSoftVerify;
    }

    @Override
    public boolean getNoImport() {
        return bSensitive;
    }

    @Override
    public String getPath() {
        return path;
    }

}
