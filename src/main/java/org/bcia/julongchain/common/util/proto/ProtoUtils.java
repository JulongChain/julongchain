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
package org.bcia.julongchain.common.util.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Collection;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/8/18
 * @company Dingxuan
 */
public class ProtoUtils {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ProtoUtils.class);

    /**
     * 从字节流解析出SmartContractID对象
     *
     * @param smartContractIDBytes
     * @return
     * @throws UnsupportedEncodingException
     * @throws InvalidProtocolBufferException
     */
    public static SmartContractPackage.SmartContractID unmarshalSmartcontractID(byte[] smartContractIDBytes)
            throws UnsupportedEncodingException, InvalidProtocolBufferException {
        SmartContractPackage.SmartContractID id = SmartContractPackage.SmartContractID.parseFrom(smartContractIDBytes);
        return id;
    }


    /**
     * 将ProposalResponse转化为字节流
     *
     * @param proposalResponse
     * @return
     */
    public static byte[] getBytesProposalResponse(ProposalResponsePackage.ProposalResponse proposalResponse) {
        //log.info("Mock getBytesProposalResponse...");
        return proposalResponse.toByteArray();
    }

    /**
     * 从字节流中解析出ProposalResponse
     *
     * @param prBytes
     * @return
     * @throws InvalidProtocolBufferException
     */
    public static ProposalResponsePackage.ProposalResponse getProposalResponse(byte[] prBytes) throws InvalidProtocolBufferException {
        return ProposalResponsePackage.ProposalResponse.parseFrom(prBytes);
    }

    // GetEnvelopeFromBlock gets an envelope from a block's Data field.
    public static Common.Envelope getEnvelopeFromBlock(byte[] block)
            throws UnsupportedEncodingException, InvalidProtocolBufferException {
        return Common.Envelope.parseFrom(block);
    }

    public static Common.Payload getPayload(Common.Envelope envelope)
            throws InvalidProtocolBufferException {
        ByteString byteString = envelope.getPayload();
        Common.Payload payload = Common.Payload.parseFrom(byteString);
        return payload;
    }

    public static Common.GroupHeader unMarshalGroupHeader(ByteString groupHeader) throws InvalidProtocolBufferException {
        return Common.GroupHeader.parseFrom(groupHeader);
    }

    public static TransactionPackage.Transaction getTransaction(ByteString data) throws InvalidProtocolBufferException {
        return TransactionPackage.Transaction.parseFrom(data);
    }

    /**
     * 从字节流从解析出ProposalResponsePackage.Response
     *
     * @param byteResponse
     * @return
     * @throws InvalidProtocolBufferException
     */
    public static ProposalResponsePackage.Response getResponse(byte[] byteResponse)
            throws InvalidProtocolBufferException {
        ProposalResponsePackage.Response response = ProposalResponsePackage.Response.parseFrom(byteResponse);
        return response;
    }

    public static byte[] marshalOrPanic(Message message) {
        return message.toByteArray();
    }

    /**
     * 从字节流中解析出SmartContractDeploymentSpec
     *
     * @param depSpecBytes
     * @return
     * @throws InvalidProtocolBufferException
     */
    public static SmartContractPackage.SmartContractDeploymentSpec getSmartContractDeploymentSpec(byte[] depSpecBytes) throws InvalidProtocolBufferException {
        return SmartContractPackage.SmartContractDeploymentSpec.parseFrom(depSpecBytes);
    }


    public static void unMarshalCollectionConfigPackage(byte[] collectionConfigBytes,
                                                        Collection.CollectionConfigPackage collections)
            throws JavaChainException {

    }

    public static SmartContractDataPackage.SmartContractData unMarshalSmartContractData(byte[] scdBytes) throws InvalidProtocolBufferException {
        return null;
    }

    /**
     * 从TxAction中解析出SCAction
     *
     * @param txActions
     * @return
     * @throws InvalidProtocolBufferException
     */
    public static ProposalPackage.SmartContractAction getSCAction(TransactionPackage.TransactionAction txActions) throws InvalidProtocolBufferException {
        TransactionPackage.SmartContractActionPayload scPayload = TransactionPackage.SmartContractActionPayload.parseFrom(txActions.getPayload());
        if (scPayload.getAction() == null || scPayload.getAction().getProposalResponsePayload() == null) {
            log.error("No payload in SCActionPayload");
            return null;
        }
        ProposalResponsePackage.ProposalResponsePayload pResPayload = ProposalResponsePackage.ProposalResponsePayload.parseFrom(scPayload.getAction().getProposalResponsePayload());
        if (pResPayload.getExtension() == null) {
            log.error("Response payload is missing extension");
            return null;
        }
        return ProposalPackage.SmartContractAction.parseFrom(pResPayload.getExtension());
    }

    public static <T extends Message> void printMessageJson(T t) {
        // 指定要写入文件的缓冲输出字节流
        BufferedOutputStream out = new BufferedOutputStream(System.out);

        InputStream is = io.grpc.protobuf.ProtoUtils.jsonMarshaller(t.getDefaultInstanceForType()).stream(t);

        // 用来存储每次读取到的字节数组
        byte[] bytes = new byte[4096];
        // 每次读取到的字节数组的长度
        int len;
        try {
            while ((len = is.read(bytes)) != -1) {
                // 写入到输出流
                out.write(bytes, 0, len);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                out.close();// 关闭流
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            try {
                is.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
