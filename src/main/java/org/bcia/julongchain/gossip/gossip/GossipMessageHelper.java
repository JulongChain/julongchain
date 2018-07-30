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
package org.bcia.julongchain.gossip.gossip;

import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.protos.gossip.Message;

public class GossipMessageHelper {

    public static Boolean isAliveMsg(Message.GossipMessage gossipMessage) {
        return gossipMessage.getAliveMsg() != null;
    }

    public static Boolean isDataMsg(Message.GossipMessage gossipMessage) {
        return gossipMessage.getDataMsg() != null;
    }

    public static Boolean isStateInfoPullRequestMsg(Message.GossipMessage gossipMessage) {
        return gossipMessage.getStateInfoPullReq() != null;
    }

    public static Boolean isStateInfoSnapshot(Message.GossipMessage gossipMessage) {
        return gossipMessage.getStateSnapshot() != null;
    }

    public static Boolean isStateInfoMsg(Message.GossipMessage gossipMessage) {
        return gossipMessage.getStateInfo() != null;
    }

    public static Boolean isPullMsg(Message.GossipMessage gossipMessage) {
        return gossipMessage.getDataReq() != null
                && gossipMessage.getDataUpdate() != null
                && gossipMessage.getHello() != null
                && gossipMessage.getDataDig() != null;
    }

    public static Boolean isRemoteStateMessage(Message.GossipMessage gossipMessage) {
        return gossipMessage.getStateRequest() != null
                && gossipMessage.getStateResponse() != null;
    }

    public static Message.PullMsgType getPullMsgType(Message.GossipMessage gossipMessage) {
        Message.GossipHello helloMsg = gossipMessage.getHello();
        if (helloMsg != null) {
            return helloMsg.getMsgType();
        }
        Message.DataDigest digMsg = gossipMessage.getDataDig();
        if (digMsg != null) {
            return digMsg.getMsgType();
        }
        Message.DataRequest reqMsg = gossipMessage.getDataReq();
        if (reqMsg != null) {
            return reqMsg.getMsgType();
        }
        Message.DataUpdate resMsg = gossipMessage.getDataUpdate();
        if (resMsg != null) {
            return resMsg.getMsgType();
        }
        return Message.PullMsgType.UNDEFINED;
    }

    public static Boolean isChannelRestricted(Message.GossipMessage gossipMessage) {
        return gossipMessage.getTag() == Message.GossipMessage.Tag.CHAN_AND_ORG
                || gossipMessage.getTag() == Message.GossipMessage.Tag.CHAN_ONLY
                || gossipMessage.getTag() == Message.GossipMessage.Tag.CHAN_OR_ORG;
    }

    public static Boolean isOrgRestricted(Message.GossipMessage gossipMessage) {
        return gossipMessage.getTag() == Message.GossipMessage.Tag.CHAN_AND_ORG
                || gossipMessage.getTag() == Message.GossipMessage.Tag.ORG_ONLY;
    }

    public static Boolean isIdentityMsg(Message.GossipMessage gossipMessage) {
        return gossipMessage.getNodeIdentity() != null;
    }

    public static Boolean isDataReq(Message.GossipMessage gossipMessage) {
        return gossipMessage.getDataReq() != null;
    }

    public static Boolean isPrivateDataMsg(Message.GossipMessage gossipMessage) {
        return gossipMessage.getPrivateReq() != null
                || gossipMessage.getPrivateRes() != null
                || gossipMessage.getPrivateData() != null;
    }

    public static Boolean isAck(Message.GossipMessage gossipMessage) {
        return gossipMessage.getAck() != null;
    }

    public static Boolean isDataUpdate(Message.GossipMessage gossipMessage) {
        return gossipMessage.getDataUpdate() != null;
    }

    public static Boolean isHelloMsg(Message.GossipMessage gossipMessage) {
        return gossipMessage.getHello() != null;
    }

    public static Boolean isDigestMsg(Message.GossipMessage gossipMessage) {
        return gossipMessage.getDataDig() != null;
    }

    public static Boolean isLeadershipMsg(Message.GossipMessage gossipMessage) {
        return gossipMessage.getLeadershipMsg() != null;
    }

    public static void isTagLegal(Message.GossipMessage gossipMessage) throws GossipException {
        if (gossipMessage.getTag() == Message.GossipMessage.Tag.UNDEFINED) {
            throw new GossipException("Undefined tag");
        }
        if (isDataMsg(gossipMessage)) {
            if (gossipMessage.getTag() != Message.GossipMessage.Tag.CHAN_AND_ORG) {
                throw new GossipException(String.format("Tag should be %s",
                        Message.GossipMessage.Tag.forNumber(Message.GossipMessage.Tag.CHAN_AND_ORG.getNumber())));
            }
            return;
        }
        if (isAliveMsg(gossipMessage) || gossipMessage.getMemReq() != null || gossipMessage.getMemRes() != null) {
            if (gossipMessage.getTag() != Message.GossipMessage.Tag.EMPTY) {
                throw new GossipException(String.format("Tag should be %s",
                        Message.GossipMessage.Tag.forNumber(Message.GossipMessage.Tag.EMPTY.getNumber())));
            }
            return;
        }
        if (isIdentityMsg(gossipMessage)) {
            if (gossipMessage.getTag() != Message.GossipMessage.Tag.ORG_ONLY) {
                throw new GossipException(String.format("Tag should be %s",
                        Message.GossipMessage.Tag.forNumber(Message.GossipMessage.Tag.ORG_ONLY.getNumber())));
            }
            return;
        }
        if (isPullMsg(gossipMessage)) {
            Message.PullMsgType pullMsgType = getPullMsgType(gossipMessage);
            switch (pullMsgType) {
                case BLOCK_MSG:
                    if (gossipMessage.getTag() != Message.GossipMessage.Tag.CHAN_AND_ORG) {
                        throw new GossipException(String.format("Tag should be %s",
                                Message.GossipMessage.Tag.forNumber(Message.GossipMessage.Tag.CHAN_AND_ORG.getNumber())));
                    }
                    return;
                case IDENTITY_MSG:
                    if (gossipMessage.getTag() != Message.GossipMessage.Tag.EMPTY) {
                        throw new GossipException(String.format("Tag should be %s",
                                Message.GossipMessage.Tag.forNumber(Message.GossipMessage.Tag.EMPTY.getNumber())));
                    }
                    return;
                default:
                    throw new GossipException(String.format("Invalid PullMsgType: %s",
                            Message.PullMsgType.forNumber(pullMsgType.getNumber())));
            }
        }
        if (isStateInfoMsg(gossipMessage) || isStateInfoPullRequestMsg(gossipMessage)
                || isStateInfoSnapshot(gossipMessage) || isRemoteStateMessage(gossipMessage)) {
            if (gossipMessage.getTag() != Message.GossipMessage.Tag.CHAN_OR_ORG) {
                throw new GossipException(String.format("Tag should be %s",
                        Message.GossipMessage.Tag.forNumber(Message.GossipMessage.Tag.CHAN_OR_ORG.getNumber())));
            }
            return;
        }
        if (isLeadershipMsg(gossipMessage)) {
            if (gossipMessage.getTag() != Message.GossipMessage.Tag.CHAN_AND_ORG) {
                throw new GossipException(String.format("Tag should be %s",
                        Message.GossipMessage.Tag.forNumber(Message.GossipMessage.Tag.CHAN_AND_ORG.getNumber())));
            }
            return;
        }
        throw new GossipException(String.format("Unknown message type: %s", gossipMessage.toString()));
    }

}
