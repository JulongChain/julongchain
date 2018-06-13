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
package org.bcia.julongchain.node.entity;

import org.bcia.julongchain.core.commiter.ICommitter;
import org.bcia.julongchain.core.node.GroupSupport;
import org.bcia.julongchain.protos.common.Common;

/**
 * 群组对象
 *
 * @author zhouhui
 * @date 2018/4/27
 * @company Dingxuan
 */
public class Group {
    private GroupSupport groupSupport;
    private Common.Block block;
    private ICommitter commiter;

    public GroupSupport getGroupSupport() {
        return groupSupport;
    }

    public void setGroupSupport(GroupSupport groupSupport) {
        this.groupSupport = groupSupport;
    }

    public Common.Block getBlock() {
        return block;
    }

    public void setBlock(Common.Block block) {
        this.block = block;
    }

    public ICommitter getCommiter() {
        return commiter;
    }

    public void setCommiter(ICommitter commiter) {
        this.commiter = commiter;
    }
}
