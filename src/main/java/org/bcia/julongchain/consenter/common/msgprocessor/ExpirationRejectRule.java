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
package org.bcia.julongchain.consenter.common.msgprocessor;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
import org.bcia.julongchain.common.groupconfig.config.IConsenterConfig;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.Expiration;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.protos.common.Common;
import org.bouncycastle.asn1.x509.Time;

import java.util.Date;
import java.util.List;

/**
 * @author zhangmingyang
 * @Date: 2018/5/9
 * @company Dingxuan
 */
public class ExpirationRejectRule implements IRule {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ExpirationRejectRule.class);
    private IGroupConfigBundle filterSupport;

    public ExpirationRejectRule(IGroupConfigBundle filterSupport) {
        this.filterSupport = filterSupport;
    }


    @Override
    public void apply(Common.Envelope message) throws ConsenterException {

        IConsenterConfig consenterConfig = filterSupport.getGroupConfig().getConsenterConfig();

        if (consenterConfig != null && consenterConfig.getCapabilities() != null && !consenterConfig.getCapabilities().isExpiration()) {
            return;
        }
        List<SignedData> signedData = null;

        try {
            signedData = SignedData.asSignedData(message);
        } catch (ValidateException e) {
            throw new ConsenterException(e);
        } catch (InvalidProtocolBufferException e) {
        }

        Date expireTime = new Expiration().expiresAt(signedData.get(0).getIdentity());
        Date nowDate = new Date();
        if (expireTime != null) {
            if (nowDate.after(expireTime)) {
                try {
                    throw new ValidateException("identity expired");
                } catch (ValidateException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

}
