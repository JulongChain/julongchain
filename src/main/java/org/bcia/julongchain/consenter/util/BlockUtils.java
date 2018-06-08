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
package org.bcia.julongchain.consenter.util;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/5/11
 * @company Dingxuan
 */
public class BlockUtils {


    public static Common.Metadata getMetadataFromBlock(Common.Block block, int index) {

        Common.Metadata md = null;
        try {
            md = Common.Metadata.parseFrom(block.getMetadata().getMetadata(index));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return md;
    }

    public static long getLastConfigIndexFromBlock(Common.Block block) {
        Common.Metadata md = getMetadataFromBlock(block, Common.BlockMetadataIndex.LAST_CONFIG_VALUE);
        Common.LastConfig lc = null;
        try {
            lc = Common.LastConfig.parseFrom(md.getValue());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return lc.getIndex();
    }

}
