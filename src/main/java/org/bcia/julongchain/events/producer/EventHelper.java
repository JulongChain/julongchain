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
package org.bcia.julongchain.events.producer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.EventException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.common.util.proto.ProposalResponseUtils;
import org.bcia.julongchain.core.ledger.util.TxValidationFlags;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述
 *
 * @author sunianle
 * @date 2018/04/25
 * @company Dingxuan
 */
public class EventHelper {
    private static JavaChainLog log = JavaChainLogFactory.getLog(EventHelper.class);

    /**
     * createBlockEvents creates block events for a block. It removes the RW set
     * and creates a block event and a filtered block event. Sending the events
     * is the responsibility of the code that calls this function.
     *
     * @param block
     * @return
     * @throws EventException
     */
    public static BlockEvents createBlockEvents(Common.Block block) throws EventException, ValidateException,
            InvalidProtocolBufferException {
        ValidateUtils.isNotNull(block, "block can not be null");
        ValidateUtils.isNotNull(block.getHeader(), "block.header can not be null");
        ValidateUtils.isNotNull(block.getMetadata(), "block.metadata can not be null");
        ValidateUtils.isNotNull(block.getData(), "block.data can not be null");
        ValidateUtils.isNotNull(block.getData().getDataList(), "block.data.dataList can not be null");

        Common.Block.Builder blockForEventBuilder = block.toBuilder();
        blockForEventBuilder.setHeader(block.getHeader());
        blockForEventBuilder.setMetadata(block.getMetadata());

        ByteString metadata = block.getMetadata().getMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER_VALUE);
        TxValidationFlags txValidationFlags = TxValidationFlags.fromByteString(metadata);

        String groupId = null;
        List<EventsPackage.FilteredTransaction> filteredTransactionList = new ArrayList<>();

        for (int i = 0; i < block.getData().getDataList().size(); i++) {
            Common.Envelope.Builder newEnvelopeBuilder = null;

            Common.Envelope envelope = Common.Envelope.parseFrom(block.getData().getData(i));
            ValidateUtils.isNotNull(envelope, "Block[" + i + "]:envelope can not be null");
            ValidateUtils.isNotNull(envelope.getPayload(), "Block[" + i + "envelope.payload can not be null");

            Common.Payload payload = Common.Payload.parseFrom(envelope.getPayload());
            ValidateUtils.isNotNull(payload.getHeader(), "Block[" + i + "envelope.payload.header can not be null");
            ValidateUtils.isNotNull(payload.getHeader().getGroupHeader(), "Block[" + i + "envelope.payload" +
                    ".groupheader can not be null");
            if (payload.getHeader().getGroupHeader() == null) {
                log.info("Block[" + i + "envelope.payload.groupheader is null");
                continue;
            }

            Common.GroupHeader groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
            if (StringUtils.isBlank(groupId) && StringUtils.isNotBlank(groupHeader.getGroupId())) {
                groupId = groupHeader.getGroupId();
            }

            switch (groupHeader.getType()) {
                case Common.HeaderType.ENDORSER_TRANSACTION_VALUE:
                    ValidateUtils.isNotNull(payload.getData(), "Block[" + i + "]:payload.data can not be null");

                    TransactionPackage.Transaction transaction = TransactionPackage.Transaction.parseFrom(payload
                            .getData());
                    TransactionPackage.Transaction.Builder newTransactionBuilder = transaction.toBuilder();
                    newTransactionBuilder.clearActions();

                    EventsPackage.FilteredTransaction.Builder filteredTransactionBuilder =
                            getFilteredTransactionBuilder(groupHeader.getTxId(), txValidationFlags, i, groupHeader
                                    .getType());

                    EventsPackage.FilteredTransactionActions.Builder transactionActionsBuilder = EventsPackage
                            .FilteredTransactionActions.newBuilder();
                    if (transaction.getActionsList() != null && transaction.getActionsCount() > 0) {
                        for (TransactionPackage.TransactionAction action : transaction.getActionsList()) {
                            TransactionPackage.SmartContractActionPayload smartContractActionPayload =
                                    TransactionPackage.SmartContractActionPayload.parseFrom(action.getPayload());
                            if (smartContractActionPayload.getAction() == null) {
                                continue;
                            }

                            ProposalResponsePackage.ProposalResponsePayload proposalResponsePayload =
                                    ProposalResponsePackage.ProposalResponsePayload.parseFrom(smartContractActionPayload
                                            .getAction().getProposalResponsePayload());
                            ProposalPackage.SmartContractAction smartContractAction = ProposalPackage
                                    .SmartContractAction.parseFrom(proposalResponsePayload.getExtension());

                            SmartContractEventPackage.SmartContractEvent smartContractEvent =
                                    SmartContractEventPackage.SmartContractEvent.parseFrom(smartContractAction.getEvents());

                            EventsPackage.FilteredSmartContractAction.Builder filteredSmartContractActionBuilder =
                                    EventsPackage.FilteredSmartContractAction.newBuilder();
                            if (StringUtils.isNotBlank(smartContractEvent.getSmartContractId())) {
                                SmartContractEventPackage.SmartContractEvent.Builder newSmartContractEventBuilder =
                                        smartContractEvent.toBuilder();
                                newSmartContractEventBuilder.clearPayload();
                                filteredSmartContractActionBuilder.setSmartContractEvent(newSmartContractEventBuilder);
                            }
                            transactionActionsBuilder.addSmartContractActions(filteredSmartContractActionBuilder);

                            ProposalResponsePackage.ProposalResponsePayload newProposalResponsePayload =
                                    ProposalResponseUtils.buildProposalResponsePayload(smartContractAction.getEvents(),
                                            ByteString.EMPTY, smartContractAction.getResponse(), smartContractAction
                                                    .getSmartContractId(), proposalResponsePayload.getProposalHash());

                            TransactionPackage.SmartContractActionPayload.Builder payloadBuilder =
                                    smartContractActionPayload.toBuilder();
                            payloadBuilder.getActionBuilder().setProposalResponsePayload(newProposalResponsePayload.toByteString());

                            TransactionPackage.TransactionAction.Builder actionbuilder = action.toBuilder();
                            actionbuilder.setPayload(payloadBuilder.build().toByteString());

                            newTransactionBuilder.addActions(actionbuilder);
                        }
                    }

                    filteredTransactionBuilder.setTransactionActions(transactionActionsBuilder);
                    filteredTransactionList.add(filteredTransactionBuilder.build());

                    Common.Payload.Builder newPayloadBuilder = payload.toBuilder().setData(newTransactionBuilder.build()
                            .toByteString());
                    newEnvelopeBuilder = envelope.toBuilder().setPayload(newPayloadBuilder.build().toByteString());
                    break;
            }

            if (newEnvelopeBuilder != null) {
                blockForEventBuilder.getDataBuilder().addData(newEnvelopeBuilder.build().toByteString());
            } else {
                blockForEventBuilder.getDataBuilder().addData(block.getData().getData(i));
            }
        }

        EventsPackage.FilteredBlock.Builder filteredBlockBuilder = EventsPackage.FilteredBlock.newBuilder();
        filteredBlockBuilder.setGroupId(groupId);
        filteredBlockBuilder.setNumber(block.getHeader().getNumber());
        filteredBlockBuilder.addAllFilteredTransactions(filteredTransactionList);

        EventsPackage.Event blockEvent = createBlockEvent(blockForEventBuilder.build());
        EventsPackage.Event filteredBlockEvent = createFilteredBlockEvent(filteredBlockBuilder.build());
        return new BlockEvents(blockEvent, filteredBlockEvent, groupId);
    }

    private static EventsPackage.Event createBlockEvent(Common.Block block) {
        return EventsPackage.Event.newBuilder().setBlock(block).build();
    }

    private static EventsPackage.Event createFilteredBlockEvent(EventsPackage.FilteredBlock filteredBlock) {
        return EventsPackage.Event.newBuilder().setFilteredBlock(filteredBlock).build();
    }

    private static EventsPackage.FilteredTransaction.Builder getFilteredTransactionBuilder(
            String txId, TxValidationFlags txValidationFlags, int txIndex, int type) throws EventException {
        EventsPackage.FilteredTransaction.Builder filteredTransactionBuilder = EventsPackage.FilteredTransaction
                .newBuilder();

        filteredTransactionBuilder.setTxid(txId);
        filteredTransactionBuilder.setTxValidationCode(txValidationFlags.flag(txIndex));
        filteredTransactionBuilder.setTypeValue(type);

        return filteredTransactionBuilder;
    }

    /**
     * send sends the event to interested consumers
     *
     * @param events
     * @throws EventException
     */
    public static void send(BlockEvents events) throws EventException {
        EventProcessor processor = EventProcessor.getInstance(null);
        if (processor == null) {
            throw new EventException("processor is null, may be node has not started");
        }

        if (events == null || events.getBlockEvent() == null) {
            throw new EventException("BlockEvent is null");
        }

        try {
            boolean isSend = processor.send(events.getBlockEvent());
            if (!isSend) {
                throw new EventException("Can not send a event");
            }
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
        }

    }
}
