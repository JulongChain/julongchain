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
package org.bcia.javachain.core.smartcontract;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 调用智能合约的响应
 *
 * @author sunianle
 * @date 2/28/18
 * @company Dingxuan
 */
public class Response {

    private final Status status;
    private final String message;
    private final byte[] payload;

    public Response(Status status, String message, byte[] payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getPayload() {
        return payload;
    }

    public String getStringPayload() {
        return new String(payload, UTF_8);
    }

    public enum Status {
        SUCCESS(200),
        INTERNAL_SERVER_ERROR(500);

        private static final Map<Integer, Status> codeToStatus = new HashMap<>();
        private final int code;

        private Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Status forCode(int code) {
            final Status result = codeToStatus.get(code);
            if(result == null) throw new IllegalArgumentException("no status for code " + code);
            return result;
        }

        static {
            for (Status status : Status.values()) {
                codeToStatus.put(status.code, status);
            }
        }

    }

}
