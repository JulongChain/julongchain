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
package org.bcia.julongchain.consenter.common.broadcast;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.BlockFileStream;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.common.Common;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

/**
 * consenter账本测试
 *
 * @author zhangmingyang
 * @date 2018/09/25
 * @company Dingxuan
 */
public class ConsenterLedgerTest {

    @Test
    public void  verifiyConsenterLedger() throws LedgerException, InvalidProtocolBufferException {
       // BlockFileStream stream=new BlockFileStream("/opt/testData/julongchain/production/node/chains/chains/myGroup",0,0);
        BlockFileStream stream=new BlockFileStream("/var/julongchain/production/node/chains/chains/myGroup",0,0);
       // BlockFileStream stream=new BlockFileStream("/var/julongchain/production/consenter/chains/myGroup",0,0);
        Common.Block block=Common.Block.parseFrom(stream.nextBlockBytes());
        Common.Block block11=Common.Block.parseFrom(stream.nextBlockBytes());
        System.out.println(Hex.toHexString(Util.getHashBytes(block.getHeader().toByteArray())));
        System.out.println(Hex.toHexString(block11.getHeader().getPreviousHash().toByteArray()));
    }
}
