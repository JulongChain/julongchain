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
package org.bcia.javachain.core.events;


import com.google.protobuf.Message;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/17
 * @company Dingxuan
 */
public class EventsServerConfig {
    public interface IBindingInspector {
        void bind(Message protoMessage);
    }

    private long BufferSize;
    private long Timeout;
    private long TimeWindow;
    private IBindingInspector bindingInspector;

    public long getBufferSize() {
        return BufferSize;
    }

    public void setBufferSize(long bufferSize) {
        BufferSize = bufferSize;
    }

    public long getTimeout() {
        return Timeout;
    }

    public void setTimeout(long timeout) {
        Timeout = timeout;
    }

    public long getTimeWindow() {
        return TimeWindow;
    }

    public void setTimeWindow(long timeWindow) {
        TimeWindow = timeWindow;
    }

    public IBindingInspector getBindingInspector() {
        return bindingInspector;
    }

    public void setBindingInspector(IBindingInspector bindingInspector) {
        this.bindingInspector = bindingInspector;
    }
}
