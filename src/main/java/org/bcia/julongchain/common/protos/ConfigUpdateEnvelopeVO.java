/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.julongchain.common.protos;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.protos.common.Configtx;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/29
 * @company Dingxuan
 */
public class ConfigUpdateEnvelopeVO implements IProtoVO<Configtx.ConfigUpdateEnvelope> {
    private Configtx.ConfigUpdate configUpdate;
    private List<ConfigSignatureVO> configSignatureVOList;

    @Override
    public void parseFrom(Configtx.ConfigUpdateEnvelope configUpdateEnvelope) throws InvalidProtocolBufferException,
            ValidateException {
        ValidateUtils.isNotNull(configUpdateEnvelope, "configUpdateEnvelope can not be null");
        ValidateUtils.isNotNull(configUpdateEnvelope.getConfigUpdate(), "configUpdateEnvelope.getConfigUpdate can " +
                "not be null");

        this.configUpdate = Configtx.ConfigUpdate.parseFrom(configUpdateEnvelope.getConfigUpdate());

        this.configSignatureVOList = new ArrayList<>();
        if (configUpdateEnvelope.getSignaturesList() != null && configUpdateEnvelope.getSignaturesCount() > 0) {
            for (int i = 0; i < configUpdateEnvelope.getSignaturesCount(); i++) {
                ConfigSignatureVO configSignatureVO = new ConfigSignatureVO();
                configSignatureVO.parseFrom(configUpdateEnvelope.getSignatures(i));

                this.configSignatureVOList.add(configSignatureVO);
            }
        }
    }

    @Override
    public Configtx.ConfigUpdateEnvelope toProto() {
        Configtx.ConfigUpdateEnvelope.Builder builder = Configtx.ConfigUpdateEnvelope.newBuilder();
        builder.setConfigUpdate(configUpdate.toByteString());
        for (ConfigSignatureVO configSignatureVO : configSignatureVOList) {
            builder.addSignatures(configSignatureVO.toProto());
        }

        return builder.build();
    }

    public Configtx.ConfigUpdate getConfigUpdate() {
        return configUpdate;
    }

    public List<ConfigSignatureVO> getConfigSignatureVOList() {
        return configSignatureVOList;
    }

    public void toSignedDatas() {
        List<SignedData> signedDataList = new ArrayList<>();

        for (ConfigSignatureVO configSignatureVO : configSignatureVOList) {
            byte[] data = ArrayUtils.addAll(configSignatureVO.getSignatureHeader().toByteArray(),
                    configUpdate.toByteArray());
            byte[] identity = configSignatureVO.getSignatureHeader().getCreator().toByteArray();
            byte[] signature = configSignatureVO.getSignature().toByteArray();

            SignedData signedData = new SignedData(data, identity, signature);
            signedDataList.add(signedData);
        }
    }
}
