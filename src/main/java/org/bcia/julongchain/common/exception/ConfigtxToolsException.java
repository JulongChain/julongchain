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
package org.bcia.julongchain.common.exception;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/06/05
 * @company Dingxuan
 */
public class ConfigtxToolsException extends JavaChainException {
    private static final String MODULE_NAME = "[ConfigtxGen]";

    public ConfigtxToolsException() {
        super();
    }

    public ConfigtxToolsException(String message) {
        super(MODULE_NAME + message);
    }

    public ConfigtxToolsException(String message, Throwable cause) {
        super(MODULE_NAME + message, cause);
    }


    public ConfigtxToolsException(Throwable cause) {
        super(cause);
    }


    protected ConfigtxToolsException(String message, Throwable cause,
                                     boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(MODULE_NAME + message, cause, enableSuppression, writableStackTrace);
    }
}
