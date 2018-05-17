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
package org.bcia.javachain.consenter.util;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/5/11
 * @company Dingxuan
 */
public class CommonUtils {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CommonUtils.class);
    public static Common.Envelope extractEnvelop(Common.Block block, int index) {
        if (block.getData() == null) {
            log.error("No data in block");
        }
        int envelopCount = block.getData().getDataList().size();
        if (index < 0 || index >= envelopCount) {
            log.error("Envelope index out of bounds");
        }
        Common.Envelope envelope = TxUtils.GetEnvelopeFromBlock(block.getData().getData(index).toByteArray());
        return envelope;
    }

    public static Common.SignatureHeader newSignatureHeaderOrPanic(ILocalSigner signer) {
        if(signer==null){
            log.error("Invalid signer. Must be different from nil.");
        }
        Common.SignatureHeader signatureHeader= signer.newSignatureHeader();
        return signatureHeader;
    }

    public static byte[] signOrPanic(ILocalSigner signer,byte[] msg){
        if(signer==null){
            log.error("Invalid signer. Must be different from nil.");
        }
        byte[] sigma=signer.sign(msg);
        return sigma;
    }

    public static  Common.Envelope extractEnvelopeOrPanic(Common.Block block,int index){
        Common.Envelope envelope=extractEnvelop(block,index);
        return envelope;
    }

    public static Common.Payload unmarshalPayload(byte[] encoded){
        Common.Payload payload=null;
        try {
            payload =Common.Payload.parseFrom(encoded);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return payload;
    }

    public static Common.GroupHeader unmarshalGroupHeader(byte[] bytes){
        Common.GroupHeader groupHeader=null;
        try {
            groupHeader=Common.GroupHeader.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return groupHeader;
    }
}
