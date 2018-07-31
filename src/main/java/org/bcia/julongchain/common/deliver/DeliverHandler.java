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
package org.bcia.julongchain.common.deliver;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.ledger.blockledger.IIterator;
import org.bcia.julongchain.common.ledger.blockledger.file.FileLedgerIterator;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.Expiration;
import org.bcia.julongchain.common.util.proto.ProtoUtils;
import org.bcia.julongchain.consenter.common.cmd.impl.StartCmd;
import org.bcia.julongchain.consenter.common.multigroup.ChainSupport;
import org.bcia.julongchain.consenter.util.CommonUtils;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.gossip.GossipServiceUtil;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;

import java.util.Date;
import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/5/29
 * @company Dingxuan
 */
public class DeliverHandler implements IHandler {
    private static JavaChainLog log = JavaChainLogFactory.getLog(DeliverHandler.class);
    private ISupportManager sm;
    private long timeWindow;
    private boolean mutualTLS;

    public DeliverHandler(ISupportManager sm, long timeWindow) {
        this.sm = sm;
        this.timeWindow = timeWindow;
    }

    public DeliverHandler(ISupportManager sm, long timeWindow, boolean mutualTLS) {
        this.sm = sm;
        this.timeWindow = timeWindow;
        this.mutualTLS = mutualTLS;
    }

    @Override
    public void handle(DeliverServer server) throws ValidateException, InvalidProtocolBufferException, LedgerException {
        try {
            delivrBlocks(server, server.getEnvelope());
        } catch (ConsenterException e) {
            e.printStackTrace();
        }
    }

    public void delivrBlocks(DeliverServer server, Common.Envelope envelope) throws ConsenterException, ValidateException, LedgerException, InvalidProtocolBufferException {
        Common.Payload payload = null;
        try {
            payload = CommonUtils.unmarshalPayload(envelope.getPayload().toByteArray());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage());
            sendStatusReply(server, Common.Status.BAD_REQUEST);
        }
        if (payload.getHeader() == null) {
            try {
                log.warn(String.format("Malformed envelope received bad header"));
                sendStatusReply(server, Common.Status.BAD_REQUEST);
            } catch (InvalidProtocolBufferException e) {
                log.error(e.getMessage());
            }
        }
        Common.GroupHeader chdr = CommonUtils.unmarshalGroupHeader(payload.getHeader().getGroupHeader().toByteArray());
        if (chdr == null) {
            try {
                sendStatusReply(server, Common.Status.BAD_REQUEST);
            } catch (InvalidProtocolBufferException e) {
                log.error(e.getMessage());
            }
        }
        try {
            validateGroupHeader(server, chdr);
        } catch (ValidateException e) {
            sendStatusReply(server, Common.Status.BAD_REQUEST);
            log.error(e.getMessage());
        }
        ChainSupport chain = sm.getChain(chdr.getGroupId());
        if (chain == null) {
            log.debug(String.format("Rejecting deliver  channel %s not found", chdr.getGroupId()));
            try {
                sendStatusReply(server, Common.Status.NOT_FOUND);
            } catch (InvalidProtocolBufferException e) {
                log.error(e.getMessage());
            }
        }
        //TODO select case  erroredChan
//        SessionAc accessControl = null;
//        try {
//            accessControl = new SessionAc(chain, server.getPolicyChecker(), chdr.getGroupId(), envelope, new Expiration());
//        } catch (InvalidProtocolBufferException e) {
//            log.error(e.getMessage());
//            sendStatusReply(server, Common.Status.BAD_REQUEST);
//        }
        //TODO 先去掉权限控制部分
//        try {
//            accessControl.enaluate();
//        } catch (ValidateException e) {
//            log.error(e.getMessage());
//            sendStatusReply(server, Common.Status.FORBIDDEN);
//        }
        Ab.SeekInfo seekInfo = null;
        try {
            seekInfo = Ab.SeekInfo.parseFrom(payload.getData().toByteArray());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            sendStatusReply(server, Common.Status.BAD_REQUEST);
            throw new InvalidProtocolBufferException(e);
        }

        if (seekInfo.getStart() == null || seekInfo.getStop() == null) {
            sendStatusReply(server, Common.Status.BAD_REQUEST);
        }

        IIterator cursor = chain.getLedgerResources().getReadWriteBase().iterator(seekInfo.getStart());
        if (cursor instanceof FileLedgerIterator) {
            FileLedgerIterator fileLedgerIterator = (FileLedgerIterator) cursor;
            long number = fileLedgerIterator.getBlockNum();
            long stopNumber = 0;
            Ab.SeekPosition.TypeCase stop = seekInfo.getStop().getTypeCase();
            switch (stop) {
                case OLDEST:
                    stopNumber = number;
                    break;
                case NEWEST:
                    stopNumber = chain.getLedgerResources().getReadWriteBase().height() - 1;
                    break;
                case SPECIFIED:
                    stopNumber = stop.getNumber();
                    if (stopNumber < number) {
                        log.warn(String.format("[channel: %s] Received invalid seekInfo message from %s: start number %d greater than stop number %d", chdr.getGroupId(), number, stopNumber));
                        sendStatusReply(server, Common.Status.BAD_REQUEST);
                    }
                    default:
            }
            {
                if (seekInfo.getBehavior() == Ab.SeekInfo.SeekBehavior.FAIL_IF_NOT_READY) {
                    if (number > chain.getLedgerResources().getReadWriteBase().height() - 1) {
                        sendStatusReply(server, Common.Status.NOT_FOUND);
                    }
                }
                QueryResult queryResult = nextBlock(cursor);
                Map.Entry<QueryResult, Common.Status> block= (Map.Entry<QueryResult, Common.Status>) queryResult.getObj();
                cursor.close();
                Common.Block blockData = (Common.Block) block.getKey().getObj();
                Common.Status status = block.getValue();
                blockData.getHeader().getNumber();
                if (status != Common.Status.SUCCESS) {
                    log.error(String.format("[channel: %s] Error reading from channel, cause was: %s", chdr.getGroupId(), status));
                    sendStatusReply(server, status);
                }
                number++;
                //TODO 去掉权限控制部分
//                try {
//                    accessControl.enaluate();
//                } catch (ValidateException e) {
//                    sendStatusReply(server, Common.Status.FORBIDDEN);
//                }
                log.debug(String.format("[channel: %s] Delivering block for %s", chdr.getGroupId(), seekInfo));
                try {
                   // ProtoUtils.printMessageJson(blockData);
                   // GossipServiceUtil.addData(StartCmd.getGossipService(),chdr.getGroupId(),blockData.getHeader().getNumber(),blockData.toString());
                    sendBlockReply(server, blockData);
                } catch (InvalidProtocolBufferException e) {
                    log.error(e.getMessage());
                }
//                catch (GossipException e) {
//                    e.printStackTrace();
//                }
                if (stopNumber == blockData.getHeader().getNumber()) {
                    return;
                }
            }
            try {
                sendStatusReply(server, Common.Status.SUCCESS);
            } catch (InvalidProtocolBufferException e) {
                log.error(e.getMessage());
            }
        }


    }


    public QueryResult nextBlock(IIterator cursor) throws LedgerException {
        // FIXME: 2018/5/31
        return cursor.next();
    }

    public void validateGroupHeader(DeliverServer server, Common.GroupHeader chdr) throws ConsenterException, ValidateException {
        if (chdr.getTimestamp() == null) {
            throw new ConsenterException("group header in envelope must contain timestamp");
        }
        Date nowDate = new Date();
        if (chdr.getTimestamp() != null) {
            long nowMilliseconds = nowDate.getTime();
            long chdrMilliseconds = chdr.getTimestamp().getSeconds() * 1000 + chdr.getTimestamp().getNanos() / 1000000;
            if (nowMilliseconds - chdrMilliseconds > timeWindow) {
                throw new ValidateException("out of range");
            }
        }

    }

    public void sendStatusReply(DeliverServer srv, Common.Status status) throws InvalidProtocolBufferException {
        srv.send(new DeliverHandlerSupport().createStatusReply(status));
    }

    public void sendBlockReply(DeliverServer srv, Common.Block block) throws InvalidProtocolBufferException {
        log.info("send the block");
        srv.send(new DeliverHandlerSupport().createBlockReply(block));
    }

    public ISupportManager getSm() {
        return sm;
    }

    public long getTimeWindow() {
        return timeWindow;
    }

}
