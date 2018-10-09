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
 * JoinChannelMessage 维护一个通道内的成员表的创建和更改和在节点之间进行数据传递
 *
 *
 * @author wanliangbing
 * @date 2018/08/20
 * @company Dingxuan
 */
public interface IJoinChannelMessage {

    /**
     * SequenceNumber 返回JoinChannelMessage的起源配置区块的序列号
     *
     * @return
     */
    public Long sequenceNumber();

    /**
     * Members 返回通道内的公司成员
     * @return
     */
    public byte[][] members();

    /**
     * AnchorNodesOf 返回公司锚节点
     * @param orgIdentity
     * @return
     */
    public AnchorNode[] anchorNodesOf(byte[] orgIdentity);

}
