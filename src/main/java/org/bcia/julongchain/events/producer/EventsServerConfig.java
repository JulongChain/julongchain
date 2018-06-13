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
package org.bcia.julongchain.events.producer;


import com.google.protobuf.Message;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/17
 * @company Dingxuan
 */
public class EventsServerConfig {
    private static final long DEFAULT_TIME_WINDOW = 900000;//900秒，15分钟

    public interface IBindingInspector {
        void bind(Message protoMessage);
    }

    private long bufferSize;
    private long timeout;
    private long timeWindow;
    private IBindingInspector bindingInspector;

    public EventsServerConfig(long bufferSize, long timeout, long timeWindow, IBindingInspector bindingInspector) {
        this.bufferSize = bufferSize;
        this.timeout = timeout;

        if (timeWindow == 0L) {
            timeWindow = DEFAULT_TIME_WINDOW;
        }
        this.timeWindow = timeWindow;

        this.bindingInspector = bindingInspector;
    }

    public long getBufferSize() {
        return bufferSize;
    }

    public long getTimeout() {
        return timeout;
    }

    public long getTimeWindow() {
        return timeWindow;
    }

    public IBindingInspector getBindingInspector() {
        return bindingInspector;
    }
}
