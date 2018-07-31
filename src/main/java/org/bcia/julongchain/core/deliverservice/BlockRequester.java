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
package org.bcia.julongchain.core.deliverservice;

import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.localmsp.impl.LocalSigner;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.proto.TxUtils;
import org.bcia.julongchain.core.deliverservice.blocksprovider.IBlocksDeliverer;
import org.bcia.julongchain.core.deliverservice.blocksprovider.ILedgerInfo;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;

/**
 * @author zhangmingyang
 * @Date: 2018/6/1
 * @company Dingxuan
 */
public class BlockRequester {
    private static JavaChainLog log = JavaChainLogFactory.getLog(BlockRequester.class);
    private boolean tls;
    private String groupId;
    private IBlocksDeliverer client;


    public void requestBlocks(ILedgerInfo ledgerInfo) throws ConsenterException {
        long height = ledgerInfo.getLedgerHeight();
        if (height > 0) {
            log.debug(String.format("Starting deliver with block [%d] for channel %s", height, groupId));
            seekLatestFromCommitter(height);
        } else {
            log.debug(String.format("Starting deliver with oldest block for channel %s", groupId));
        }
    }


    public void seekOldest() {
        Ab.SeekPosition.Builder seekStartPsistion = Ab.SeekPosition.newBuilder();
        Ab.SeekPosition.Builder seekStopPsistion = Ab.SeekPosition.newBuilder();
        Ab.SeekOldest.Builder seekOldest = Ab.SeekOldest.newBuilder();
        Ab.SeekSpecified.Builder seekSpecified = Ab.SeekSpecified.newBuilder();
        //TODO math.MaxUnit64
        seekSpecified.setNumber(Long.MAX_VALUE);
        seekStartPsistion.setOldest(seekOldest);
        seekStopPsistion.setSpecified(seekSpecified);
        Ab.SeekInfo seekInfo = Ab.SeekInfo.newBuilder()
                .setStart(seekStartPsistion)
                .setStop(seekStopPsistion)
                .setBehavior(Ab.SeekInfo.SeekBehavior.BLOCK_UNTIL_READY).build();

        int msgVersion = 0;
        long epoch = 0;
        byte[] tlsCertHash = getTLSCertHash();

       Common.Envelope envelope= TxUtils.createSignedEnvelopeWithTLSBinding(Common.HeaderType.DELIVER_SEEK_INFO_VALUE,groupId,new LocalSigner(),seekInfo,msgVersion,epoch,tlsCertHash);
       client.send(envelope);
    }


    public void seekLatestFromCommitter(long height) {
        Ab.SeekPosition.Builder seekStartPsistion = Ab.SeekPosition.newBuilder();
        Ab.SeekPosition.Builder seekStopPsistion = Ab.SeekPosition.newBuilder();
        Ab.SeekSpecified.Builder seekStartSpecified = Ab.SeekSpecified.newBuilder();
        seekStartSpecified.setNumber(height);
        Ab.SeekSpecified.Builder seekStopSpecified = Ab.SeekSpecified.newBuilder();
        seekStopSpecified.setNumber(Long.MAX_VALUE);
        seekStartPsistion.setSpecified(seekStartSpecified);
        seekStopPsistion.setSpecified(seekStopSpecified);
        Ab.SeekInfo seekInfo = Ab.SeekInfo.newBuilder()
                .setStart(seekStartPsistion)
                .setStop(seekStopPsistion)
                .setBehavior(Ab.SeekInfo.SeekBehavior.BLOCK_UNTIL_READY).build();
        int msgVersion = 0;
        long epoch = 0;
        byte[] tlsCertHash = getTLSCertHash();
        Common.Envelope envelope= TxUtils.createSignedEnvelopeWithTLSBinding(Common.HeaderType.DELIVER_SEEK_INFO_VALUE,groupId,new LocalSigner(),seekInfo,msgVersion,epoch,tlsCertHash);
        client.send(envelope);

    }

    public byte[] getTLSCertHash() {
        if (tls) {
            return null;
        }
        return null;
    }

    public BlockRequester(boolean tls, String groupId, IBlocksDeliverer client) {
        this.tls = tls;
        this.groupId = groupId;
        this.client = client;
    }

    public boolean isTls() {
        return tls;
    }

    public String getGroupId() {
        return groupId;
    }

    public IBlocksDeliverer getClient() {
        return client;
    }
}
