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
package org.bcia.javachain.common.ledger.blkstorage.fsblkstorage;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/8
 * @company Dingxuan
 */
public class BlockStream {

    private String rootDir;
    private Integer currentFileNum;
    private Integer endFileNum;
    private BlockfileStream currentFileStream;

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public Integer getCurrentFileNum() {
        return currentFileNum;
    }

    public void setCurrentFileNum(Integer currentFileNum) {
        this.currentFileNum = currentFileNum;
    }

    public Integer getEndFileNum() {
        return endFileNum;
    }

    public void setEndFileNum(Integer endFileNum) {
        this.endFileNum = endFileNum;
    }

    public BlockfileStream getCurrentFileStream() {
        return currentFileStream;
    }

    public void setCurrentFileStream(BlockfileStream currentFileStream) {
        this.currentFileStream = currentFileStream;
    }
}
