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

import org.bcia.julongchain.common.exception.SarException;

import java.io.UnsupportedEncodingException;
import java.util.List;

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
    private String sAppName;
    private long lAppHandle;
    private String sUserPin;


    public GMT0016FactoryOpts(GMT0016Lib gmt0016Lib/*, GMT0016Config gmt0016Conf*/) {
        mSKF = new SKFFactoryOpts();
        sUserPin = gmt0016Lib.getUserPin();
        init(gmt0016Lib);
    }

    private void init(GMT0016Lib lib) {

        try {

            mSKF.InitSKF(lib.getLibrary());
            List<String> devNamesList = mSKF.SKF_EnumDevs(true);

            String tempdevName = "";
            for(String devName : devNamesList) {
                tempdevName = devName;
                long devHandle = mSKF.SKF_ConnectDev(tempdevName);
                lDevHandle = devHandle;
                SKFDeviceInfo devinfo = mSKF.SKF_GetDevInfo(devHandle);
                String sLabel = new String(new String(devinfo.getLabel()).getBytes("iso-8859-1"), "utf-8");
                String sSn = new String(new String(devinfo.getSerialnumber()));
                if(lib.getKeyLabel().equals(sLabel.trim()) && lib.getKeySN().equals(sSn.trim())) {
                    List<String> appNamesList = mSKF.SKF_EnumApplication(devHandle);
                    String tempappName = "";
                    for(String appName : appNamesList) {
                        tempappName = appName;
                        //open application, save the handle
                        lAppHandle = mSKF.SKF_OpenApplication(devHandle, tempappName);
                        break;
                    }
                    sAppName = tempappName;
                    break;
                }
            }

        }catch(SarException ex) {
            if(ex.getErrorCode() == SarException.SAR_APPLICATION_NOT_EXIST)
            {
                try {
                    mSKF.SKF_DisconnectDev(lDevHandle);
                }catch(SarException e) {
                    e.printStackTrace();
                }
            }
            else
                ex.printStackTrace();
        }catch(JCSKFException ex) {
            if(ex.getErrCode() == JCSKFException.JC_SKF_NOAPP)
            {
                try {
                    mSKF.SKF_DisconnectDev(lDevHandle);
                }catch(SarException e) {
                    e.printStackTrace();
                }
            }
            else
                ex.printStackTrace();
        }catch(UnsupportedEncodingException ex) {
            ex.printStackTrace();
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
