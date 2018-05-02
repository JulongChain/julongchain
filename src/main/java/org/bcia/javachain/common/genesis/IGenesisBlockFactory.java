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
package org.bcia.javachain.common.genesis;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.protos.common.Common;

/**
 * 创世区块工厂接口
 *
 * @author zhouhui
 * @date 2018/4/25
 * @company Dingxuan
 */
public interface IGenesisBlockFactory {
    Common.Block getGenesisBlock(String groupId) throws JavaChainException;
}
