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
package org.bcia.julongchain.common.util;

import org.bcia.julongchain.common.exception.ValidateException;

/**
 * 网络地址
 *
 * @author zhouhui
 * @date 2018/05/16
 * @company Dingxuan
 */
public class NetAddress {
    private String host;
    private int port;

    public NetAddress(String address) throws ValidateException {
        ValidateUtils.isNotBlank(address, "address can not be empty");

        String[] hostAndPort = address.split(":");
        if (hostAndPort.length <= 1) {
            throw new ValidateException("Wrong address");
        }

        host = hostAndPort[0];

        try {
            port = Integer.parseInt(hostAndPort[1]);
        } catch (Exception ex) {
            throw new ValidateException("Wrong address.port", ex);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
