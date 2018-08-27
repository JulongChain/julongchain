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
package org.bcia.julongchain.gossip.comm;

import org.apache.commons.lang3.BooleanUtils;
import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.gossip.gossip.ConnectionInfo;
import org.bcia.julongchain.gossip.gossip.SignedGossipMessage;
import org.bcia.julongchain.gossip.util.MiscUtil;

/**
 * class description
 *
 * @author wanliangbing
 * @date 18-7-24
 * @company Dingxuan
 */
public class Connection {

    private static final JulongChainLog log = JulongChainLogFactory.getLog(Connection.class);
    private ConnectionInfo info;
    private Channel<MsgSending> outBuff;
    private byte[] pkiID;
    private IHandler handler;
    private Boolean stopFlag;
    private Channel<Object> stopChan;

    public void close() {

    }

    public Boolean toDie() {
        return BooleanUtils.isTrue(stopFlag);
    }

    public synchronized void send(SignedGossipMessage msg, Boolean shouldBlock) {
        if (toDie()) {
            log.debug("Aborting send() to " + getInfo().getEndpoint().toString() + " because connection is closing");
            return;
        }
        MsgSending msgSending = new MsgSending();
        msgSending.setEnvelope(msg.getEnvelope());
        Channel<MsgSending> outBuff = this.getOutBuff();
        Integer sendBuffSize = MiscUtil.getIntOrDefault("node.gossip.sendBuffSize", CommImpl.defSendBuffSize);
        if (outBuff.stream().count() == sendBuffSize.longValue()) {
            log.debug("Buffer to " + getInfo().getEndpoint().toString() + " overflowed, drooping message", msg.toString() );
            if (!shouldBlock) {
                return;
            }
        }
        try {
            getOutBuff().take();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void serviceConnection() throws GossipException {

    }

    public static JulongChainLog getLog() {
        return log;
    }

    public ConnectionInfo getInfo() {
        return info;
    }

    public void setInfo(ConnectionInfo info) {
        this.info = info;
    }

    public Channel<MsgSending> getOutBuff() {
        return outBuff;
    }

    public void setOutBuff(Channel<MsgSending> outBuff) {
        this.outBuff = outBuff;
    }

    public byte[] getPkiID() {
        return pkiID;
    }

    public void setPkiID(byte[] pkiID) {
        this.pkiID = pkiID;
    }

    public IHandler getHandler() {
        return handler;
    }

    public void setHandler(IHandler handler) {
        this.handler = handler;
    }

    public Boolean getStopFlag() {
        return stopFlag;
    }

    public void setStopFlag(Boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    public Channel<Object> getStopChan() {
        return stopChan;
    }

    public void setStopChan(Channel<Object> stopChan) {
        this.stopChan = stopChan;
    }
}
