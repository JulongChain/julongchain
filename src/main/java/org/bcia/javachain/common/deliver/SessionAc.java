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
package org.bcia.javachain.common.deliver;


import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.util.Expiration;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.protos.common.Common;
import org.bouncycastle.asn1.x509.Time;

import java.util.Date;
import java.util.List;

/**
 * @author zhangmingyang
 * @Date: 2018/5/30
 * @company Dingxuan
 */
public class SessionAc {
    private IAcSupport acSupport;
    private IPolicyChecker checkPolicy;
    private String group;
    private Common.Envelope envelope;
    private long lastConfigSequence;
    private Time sessionEndTime;
    private boolean usedAtLeastOnce;

    public SessionAc(IAcSupport acSupport, IPolicyChecker checkPolicy, String group, Common.Envelope envelope, Time time){
        List<SignedData> signedData=null;
        try {
           signedData= SignedData.asSignedData(envelope);
        } catch (ValidateException e) {
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        this.acSupport = acSupport;
        this.checkPolicy = checkPolicy;
        this.group = group;
        this.envelope = envelope;
        this.sessionEndTime = Expiration.expiresAt(signedData.get(0).getIdentity());
    }

    public void enaluate() throws ValidateException {
        Date nowDate = new Date();
        if(nowDate.after(sessionEndTime.getDate())&&!sessionEndTime.getDate().equals(null)){
            throw new ValidateException(String.format("client identity expired %v before",sessionEndTime));
        }
        boolean policyCheckNeeded=!usedAtLeastOnce;
        long currentConfigSequence=acSupport.sequence();
        if(currentConfigSequence>lastConfigSequence){
            lastConfigSequence=currentConfigSequence;
            policyCheckNeeded=true;
        }
        if (!policyCheckNeeded){
            return;
        }
        usedAtLeastOnce=true;
        checkPolicy.policyChecker(envelope,group);
    }

}
