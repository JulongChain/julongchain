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
package org.bcia.javachain.consenter.consensus;

import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/3/7
 * @company Dingxuan
 */
public interface IConsenterSupport extends ILocalSigner, IProcessor {
    IReceiver blockCutter();

    IOrderer sharedConfig();

    Common.Block createNextBlock(Common.Envelope[] messages);

    void writeBlock(Common.Block block, byte[] encodedMetadataValue);

    void writeConfig(Common.Block block, byte[] encodedMetadataValue);

    long sequence();

    String chainID();

    long height();
}
