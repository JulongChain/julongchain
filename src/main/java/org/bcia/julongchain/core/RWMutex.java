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
package org.bcia.julongchain.core;

import org.bcia.julongchain.core.container.Mutex;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/2
 * @company Dingxuan
 */
public class RWMutex {

    private Mutex w;
    private Integer writerSem;
    private Integer readerSem;
    private Integer readerCount;
    private Integer readerWait;

    public Mutex getW() {
        return w;
    }

    public void setW(Mutex w) {
        this.w = w;
    }

    public Integer getWriterSem() {
        return writerSem;
    }

    public void setWriterSem(Integer writerSem) {
        this.writerSem = writerSem;
    }

    public Integer getReaderSem() {
        return readerSem;
    }

    public void setReaderSem(Integer readerSem) {
        this.readerSem = readerSem;
    }

    public Integer getReaderCount() {
        return readerCount;
    }

    public void setReaderCount(Integer readerCount) {
        this.readerCount = readerCount;
    }

    public Integer getReaderWait() {
        return readerWait;
    }

    public void setReaderWait(Integer readerWait) {
        this.readerWait = readerWait;
    }
}
