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
package org.bcia.javachain.consenter.common.msgprocessor;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.channelconfig.IResources;
import org.bcia.javachain.common.exception.ConsenterException;
import org.bcia.javachain.common.groupconfig.config.IConsenterConfig;
import org.bcia.javachain.consenter.util.SignData;
import org.bcia.javachain.protos.common.Common;
import org.bouncycastle.util.Times;

import java.sql.Date;
import java.sql.Time;

import static org.bcia.javachain.common.util.Expiration.expiresAt;

/**
 * @author zhangmingyang
 * @Date: 2018/5/9
 * @company Dingxuan
 */
public class Expiration implements IRule {

    private IConsenterConfig filterSupport;

    public Expiration(IConsenterConfig filterSupport) {
        this.filterSupport = filterSupport;
    }


    @Override
    public void apply(Common.Envelope message) {
        IConsenterConfig consenterConf = filterSupport;
        SignData signData = null;

        if (!consenterConf.getCapabilities().isExpiration()) {
        }
        try {
            signData =SignData.asSignedData(message);


        } catch (ConsenterException e) {
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        java.util.Date date=new java.util.Date();

        org.bouncycastle.asn1.x509.Time time=new org.bouncycastle.asn1.x509.Time(date);
        org.bouncycastle.asn1.x509.Time expirationTime=expiresAt(signData.getIdentity());
//        if(expirationTime>){
//
//        }
    }


}
