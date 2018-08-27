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
package org.bcia.julongchain.core.common.grpc;

/**
 * 保活选项
 *
 * @author zhouhui
 * @date 2018/3/20
 * @company Dingxuan
 */
public class KeepAliveOptions {
    /**
     * 客户端间隔时间，在该时间内如果客户端没有察觉服务器的任何活动，它将ping服务器，以检测其是否处于活动状态
     */
    private long clientInterval;
    /**
     * 客户端超时时间，客户端发送ping信号后等待服务器响应的持续时间
     */
    private long clientTimeout;
    /**
     * 服务器间隔时间，在该时间内如果服务器没有察觉客户端的任何活动，它将ping客户端，以检测其是否处于活动状态
     */
    private long serverInterval;
    /**
     * 服务器超时时间，服务器发送ping信号后等待客户端响应的持续时间
     */
    private long serverTimeout;

    /**
     * 服务器最小间隔时间。如果客户端发送的ping信号太频繁，服务器就会断开连接。该属性用来保护服务器，防止客户端过于频繁的攻击
     */
    private long serverMinInterval;

    public long getClientInterval() {
        return clientInterval;
    }

    public void setClientInterval(long clientInterval) {
        this.clientInterval = clientInterval;
    }

    public long getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(long clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public long getServerInterval() {
        return serverInterval;
    }

    public void setServerInterval(long serverInterval) {
        this.serverInterval = serverInterval;
    }

    public long getServerTimeout() {
        return serverTimeout;
    }

    public void setServerTimeout(long serverTimeout) {
        this.serverTimeout = serverTimeout;
    }

    public long getServerMinInterval() {
        return serverMinInterval;
    }

    public void setServerMinInterval(long serverMinInterval) {
        this.serverMinInterval = serverMinInterval;
    }
}
