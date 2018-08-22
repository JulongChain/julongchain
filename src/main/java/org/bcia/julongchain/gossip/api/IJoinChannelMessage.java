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
package org.bcia.julongchain.gossip.api;

/**
 * JoinChannelMessage is the message that asserts a creation or mutation
 * of a channel's membership list, and is the message that is gossipped
 * among the nodes
 *
 * @author wanliangbing
 * @date 2018/08/20
 * @company Dingxuan
 */
public interface IJoinChannelMessage {

    /**
     * SequenceNumber returns the sequence number of the configuration block
     * the JoinChannelMessage originated from
     * @return
     */
    public Long sequenceNumber();

    /**
     * Members returns the organizations of the channel
     * @return
     */
    public byte[][] members();

    /**
     * AnchorNodesOf returns the anchor nodes of the given organization
     * @param orgIdentity
     * @return
     */
    public AnchorNode[] anchorNodesOf(byte[] orgIdentity);

}
