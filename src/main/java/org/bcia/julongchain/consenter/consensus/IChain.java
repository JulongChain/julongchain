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
package org.bcia.julongchain.consenter.consensus;

import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/3/7
 * @company Dingxuan
 */
public interface IChain {
    // NOTE: The kafka consenter has not been updated to perform the revalidation
    // checks conditionally.  For now, Order/Configure are essentially Enqueue as before.
    // This does not cause data inconsistency, but it wastes cycles and will be required
    // to properly support the ConfigUpdate concept once introduced
    // Once this is done, the MsgClassification logic in msgprocessor should return error
    // for non ConfigUpdate/Normal msg types

    // Order accepts a message which has been processed at a given configSeq.
    // If the configSeq advances, it is the responsibility of the consenter
    // to revalidate and potentially discard the message
    // The consenter may return an error, indicating the message was not accepted
    void order(Common.Envelope env, long configSeq);
    // Configure accepts a message which reconfigures the channel and will
    // trigger an update to the configSeq if committed.  The configuration must have
    // been triggered by a ConfigUpdate message. If the config sequence advances,
    // it is the responsibility of the consenter to recompute the resulting config,
    // discarding the message if the reconfiguration is no longer valid.
    // The consenter may return an error, indicating the message was not accepted
    void configure(Common.Envelope config, long configSeq);
    // WaitReady blocks waiting for consenter to be ready for accepting new messages.
    // This is useful when consenter needs to temporarily block ingress messages so
    // that in-flight messages can be consumed. It could return error if consenter is
    // in erroneous states. If this blocking behavior is not desired, consenter could
    // simply return nil.
    void waitReady() throws ConsenterException;

    /**
     * 未定义方法Errored() <-chan struct{}
     */


    // Start should allocate whatever resources are needed for staying up to date with the chain.
    // Typically, this involves creating a thread which reads from the ordering source, passes those
    // messages to a block cutter, and writes the resulting blocks to the ledger.
    void start();
    // Halt frees the resources which were allocated for this Chain.
    void halt();
}
