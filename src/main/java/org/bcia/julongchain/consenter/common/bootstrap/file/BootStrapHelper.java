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
package org.bcia.julongchain.consenter.common.bootstrap.file;

import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.consenter.common.bootstrap.IHelper;
import org.bcia.julongchain.protos.common.Common;

import java.io.IOException;

/**
 * @author zhangmingyang
 * @Date: 2018/5/9
 * @company Dingxuan
 */
public class BootStrapHelper implements IHelper {
    private String genesisBlockFile;

    public BootStrapHelper(String genesisBlockFile) {
        this.genesisBlockFile = genesisBlockFile;
    }

    @Override
    public Common.Block getGenesisBlock() {
        Common.Block gensisBlock = null;
        try {
           byte[] bootstrapFile=FileUtils.readFileBytes(genesisBlockFile);
            gensisBlock=Common.Block.parseFrom(bootstrapFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gensisBlock;
    }
}
