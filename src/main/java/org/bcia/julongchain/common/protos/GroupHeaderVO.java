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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/26
 * @company Dingxuan
 */
public class GroupHeaderVO implements IProtoVO<Common.GroupHeader> {
    private int type;
    private int version;
    private Timestamp timeStamp;
    private String groupId;
    private String txId;
    private long epoch;
    private ProposalPackage.SmartContractHeaderExtension groupHeaderExtension;
    private ByteString tlsCertHash;

    @Override
    public void parseFrom(Common.GroupHeader groupHeader) throws InvalidProtocolBufferException, ValidateException {
        ValidateUtils.isNotNull(groupHeader, "payload can not be null");

        this.type = groupHeader.getType();
        this.version = groupHeader.getVersion();
        this.timeStamp = groupHeader.getTimestamp();
        this.groupId = groupHeader.getGroupId();
        this.txId = groupHeader.getTxId();
        this.epoch = groupHeader.getEpoch();

        if (type == Common.HeaderType.ENDORSER_TRANSACTION_VALUE || groupHeader.getType() == Common.HeaderType
                .CONFIG_VALUE) {
            ValidateUtils.isNotNull(groupHeader.getExtension(), "payload.extension can not be null");
            this.groupHeaderExtension = ProposalPackage.SmartContractHeaderExtension.parseFrom(groupHeader.getExtension());
        }
        this.tlsCertHash = groupHeader.getTlsCertHash();
    }

    @Override
    public Common.GroupHeader toProto() {
        Common.GroupHeader.Builder builder = Common.GroupHeader.newBuilder();
        builder.setType(type);
        builder.setVersion(version);
        builder.setTimestamp(timeStamp);
        builder.setGroupId(groupId);
        builder.setTxId(txId);
        builder.setEpoch(epoch);
        if (groupHeaderExtension != null) {
            builder.setExtension(groupHeaderExtension.toByteString());
        }
        builder.setTlsCertHash(tlsCertHash);
        return builder.build();
    }

    public int getType() {
        return type;
    }

    public int getVersion() {
        return version;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getTxId() {
        return txId;
    }

    public long getEpoch() {
        return epoch;
    }

    public ProposalPackage.SmartContractHeaderExtension getGroupHeaderExtension() {
        return groupHeaderExtension;
    }

    public ByteString getTlsCertHash() {
        return tlsCertHash;
    }


}
