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
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.common.Common;

/**
 * 头部业务对象
 *
 * @author zhouhui
 * @date 2018/09/30
 * @company Dingxuan
 */
public class HeaderVO implements IProtoVO<Common.Header> {
    private GroupHeaderVO groupHeaderVO;
    private Common.SignatureHeader signatureHeader;

    @Override
    public void parseFrom(Common.Header header) throws InvalidProtocolBufferException, ValidateException {
        ValidateUtils.isNotNull(header, "Header can not be null");
        ValidateUtils.isNotNull(header.getGroupHeader(), "Header.getGroupHeader can not be null");
        ValidateUtils.isNotNull(header.getSignatureHeader(), "Header.getSignatureHeader can not be null");

        this.groupHeaderVO = new GroupHeaderVO();
        Common.GroupHeader groupHeader = Common.GroupHeader.parseFrom(header.getGroupHeader());
        groupHeaderVO.parseFrom(groupHeader);

        this.signatureHeader = Common.SignatureHeader.parseFrom(header.getSignatureHeader());
    }

    @Override
    public Common.Header toProto() {
        Common.Header.Builder builder = Common.Header.newBuilder();
        builder.setGroupHeader(groupHeaderVO.toProto().toByteString());
        builder.setSignatureHeader(signatureHeader.toByteString());

        return builder.build();
    }

    public GroupHeaderVO getGroupHeaderVO() {
        return groupHeaderVO;
    }

    public Common.SignatureHeader getSignatureHeader() {
        return signatureHeader;
    }
}
