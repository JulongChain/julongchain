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
 * GRPC客户端配置
 *
 * @author zhouhui
 * @date 2018/3/20
 * @company Dingxuan
 */
public class GrpcClientConfig {
    private SecureOptions secureOptions;

    private KeepAliveOptions keepaliveOptions;
    /**
     * 当客户端尝试建立连接时，将阻塞的时间
     */
    private long timeout;

    public SecureOptions getSecureOptions() {
        return secureOptions;
    }

    public void setSecureOptions(SecureOptions secureOptions) {
        this.secureOptions = secureOptions;
    }

    public KeepAliveOptions getKeepaliveOptions() {
        return keepaliveOptions;
    }

    public void setKeepaliveOptions(KeepAliveOptions keepaliveOptions) {
        this.keepaliveOptions = keepaliveOptions;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
