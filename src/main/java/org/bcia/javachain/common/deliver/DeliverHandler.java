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
package org.bcia.javachain.common.deliver;

import org.bcia.javachain.common.exception.ConsenterException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.util.CommonUtils;
import org.bcia.javachain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/5/29
 * @company Dingxuan
 */
public class DeliverHandler implements IHandle{
    private static JavaChainLog log = JavaChainLogFactory.getLog(DeliverHandler.class);
    private ISupportManager sm;
    private long timeWindow;

    public DeliverHandler(ISupportManager sm, long timeWindow) {
        this.sm = sm;
        this.timeWindow = timeWindow;
    }

    public ISupportManager getSm() {
        return sm;
    }

    public long getTimeWindow() {
        return timeWindow;
    }

    @Override
    public void handle(DeliverServer server) {
       Common.Envelope envelope= server.getSupport().recv();
        try {
            delivrBlocks(server,envelope);
        } catch (ConsenterException e) {
            e.printStackTrace();
        }
    }

    public void delivrBlocks(DeliverServer server,Common.Envelope envelope) throws ConsenterException {
        Common.Payload payload= CommonUtils.unmarshalPayload(envelope.getPayload().toByteArray());

        sendStatusReply(server,Common.Status.BAD_REQUEST);

        if(payload.getHeader()==null){
            log.warn(String.format("Malformed envelope received bad header"));
            sendStatusReply(server,Common.Status.BAD_REQUEST);
        }
        Common.GroupHeader chdr=CommonUtils.unmarshalGroupHeader(payload.getHeader().getGroupHeader().toByteArray());

        sendStatusReply(server,Common.Status.BAD_REQUEST);

        validateGroupHeader(server,chdr);
        sendStatusReply(server,Common.Status.BAD_REQUEST);

        ISupport chain=sm.getChain(chdr.getGroupId());
        if(chain==null){
            log.debug(String.format("Rejecting deliver  channel %s not found",chdr.getGroupId()));
            sendStatusReply(server,Common.Status.NOT_FOUND);
        }

        //TODO select case  erroredChan

     //   new SessionAc(chain,envelope,server.getPolicyChecker(),chdr.getGroupId(), )

    }

    public void validateGroupHeader(DeliverServer server,Common.GroupHeader chdr) throws ConsenterException {
        if (chdr.getTimestamp()==null){
            throw new ConsenterException("group header in envelope must contain timestamp");
        }

    }

    public void sendStatusReply(DeliverServer srv,Common.Status status){
        srv.getSupport().createStatusReply(status);
    }
    public void sendBlockReply(DeliverServer srv,Common.Block block){
        srv.getSupport().createBlockReply(block);
    }

}
