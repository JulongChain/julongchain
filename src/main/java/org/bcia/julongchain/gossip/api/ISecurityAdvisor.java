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
 * SecurityAdvisor 定义一个外部附加的对象来提供安全身份相关的功能
 *
 * @author wanliangbing
 * @date 2018/08/20
 * @company Dingxuan
 */
public interface ISecurityAdvisor {

    /**
     * OrgByNodeIdentity 返回 OrgIdentityType 对应节点的身份
     * 如果发生错误，返回nil
     * 这种方法不会验证nodeIdentity
     * 验证应当在执行时完成
     * @param nodeIdentity
     * @return
     */
    public byte[] orgByNodeIdentity(byte[] nodeIdentity);

}
