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
package org.bcia.julongchain.csp.gmt0016.ftsafe;

import org.bcia.julongchain.common.exception.JCSKFException;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.SarException;
import org.bcia.julongchain.csp.gmt0016.ftsafe.entity.GMT0016Lib;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Class description
 *
 * @author
 * @date 7/3/18
 * @company FEITIAN
 */
public class GMT0016FactoryOpts implements IGMT0016FactoryOpts{

    private ISKFFactory mSKF;
    private long lDevHandle;
    private long lAppHandle;
    private String sUserPin;

    GMT0016CspLog csplog = new GMT0016CspLog();

    public GMT0016FactoryOpts(GMT0016Lib gmt0016Lib/*, GMT0016Config gmt0016Conf*/) throws JavaChainException {
        mSKF = new SKFFactoryOpts();
        sUserPin = gmt0016Lib.getUserPin();
        init(gmt0016Lib);
    }

    private void init(GMT0016Lib lib) throws JavaChainException {

        try {

            mSKF.InitSKF(lib.getLibrary());
            List<String> devNamesList = mSKF.SKF_EnumDevs(true);
            boolean bFindDev = false;
            for(String devName : devNamesList) {

                long devHandle = mSKF.SKF_ConnectDev(devName);
                lDevHandle = devHandle;
                SKFDeviceInfo devinfo = mSKF.SKF_GetDevInfo(devHandle);
                String sLabel = new String(new String(devinfo.getLabel()).getBytes("iso-8859-1"), "utf-8");
                String sSn = new String(new String(devinfo.getSerialnumber()));
                if(lib.getKeyLabel().equals(sLabel.trim()) && lib.getKeySN().equals(sSn.trim())) {
                    bFindDev = true;
                    boolean bFindApp = false;
                    List<String> appNamesList = mSKF.SKF_EnumApplication(devHandle);
                    for(String appName : appNamesList) {

                        //open application, save the handle
                        lAppHandle = mSKF.SKF_OpenApplication(devHandle, appName);
                        bFindApp = true;
                        break;
                    }
                    if(bFindApp)
                        break;
                    else {
                        String info = "[JC_SKF]:Find The Device, But No Find Application!";
                        csplog.setLogMsg(info, 2, GMT0016FactoryOpts.class);
                        throw new JavaChainException(info);
                    }
                }
            }

            if(!bFindDev)
            {
                String info = String.format("[JC_SKF]:No Find The Device! (SN: %s, Lable:%s)",
                        lib.getKeySN(), lib.getKeyLabel());
                csplog.setLogMsg(info, 2, GMT0016FactoryOpts.class);
                throw new JavaChainException(info);
            }

        }catch(SarException ex) {
            if(ex.getErrorCode() == SarException.SAR_APPLICATION_NOT_EXIST)
            {
                try {
                    mSKF.SKF_DisconnectDev(lDevHandle);
                }catch(SarException e) {
                    ex.printStackTrace();
                    String err = String.format("[JC_SKF]:SarException ErrMessage: %s", ex.getMessage());
                    csplog.setLogMsg(err, 2, GMT0016FactoryOpts.class);
                    throw new JavaChainException(err, ex.getCause());
                }
            }
            else{
                ex.printStackTrace();
                String err = String.format("[JC_SKF]:SarException ErrMessage: %s", ex.getMessage());
                csplog.setLogMsg(err, 2, GMT0016FactoryOpts.class);
                throw new JavaChainException(err, ex.getCause());
            }
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, GMT0016FactoryOpts.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(UnsupportedEncodingException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:UnsupportedEncodingException ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, GMT0016FactoryOpts.class);
            throw new JavaChainException(err, ex.getCause());
        }
    }



    @Override
    public String getProviderName() {
        return PROVIDER_GMT0016;
    }

    @Override
    public String getProviderDescription() {
        return "";
    }

    @Override
    public String getKeyStore() {
        return "";
    }

    @Override
    public void parseFrom(Map<String, String> optMap){return;}

    public ISKFFactory getSKFFactory() {
        return mSKF;
    }

    public long getAppHandle() {
        return lAppHandle;
    }


    public String getUserPin() {
        return sUserPin;
    }

    public long getDevHandle() {
        return lDevHandle;
    }

    public boolean isDefaultCsp() {
        return false;
    }

}
