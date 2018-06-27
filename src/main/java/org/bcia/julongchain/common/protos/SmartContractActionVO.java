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
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.TxRwSet;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/26
 * @company Dingxuan
 */
public class SmartContractActionVO implements IProtoVO<ProposalPackage.SmartContractAction> {
    private TxRwSet results;
    private ByteString events;
    private ProposalResponsePackage.Response response;
    private SmartContractPackage.SmartContractID smartContractID;

    @Override
    public void parseFrom(ProposalPackage.SmartContractAction smartContractAction) throws
            InvalidProtocolBufferException, ValidateException {
        ValidateUtils.isNotNull(smartContractAction, "smartContractAction can not be null");

        this.results = new TxRwSet();
        try {
            this.results.fromProtoBytes(smartContractAction.getResults());
        } catch (LedgerException e) {
            throw new ValidateException(e);
        }

        this.events = smartContractAction.getEvents();

        this.response = smartContractAction.getResponse();

        this.smartContractID = smartContractAction.getSmartContractId();
    }

    @Override
    public ProposalPackage.SmartContractAction toProto() {
        ProposalPackage.SmartContractAction.Builder builder = ProposalPackage.SmartContractAction.newBuilder();
        builder.setResults(results.toProtoBytes());
        if (events != null) {
            builder.setEvents(events);
        }
        if (response != null) {
            builder.setResponse(response);
        }
        if (smartContractID != null) {
            builder.setSmartContractId(smartContractID);
        }

        return builder.build();
    }

    public TxRwSet getResults() {
        return results;
    }

    public ByteString getEvents() {
        return events;
    }

    public ProposalResponsePackage.Response getResponse() {
        return response;
    }

    public SmartContractPackage.SmartContractID getSmartContractID() {
        return smartContractID;
    }
}
