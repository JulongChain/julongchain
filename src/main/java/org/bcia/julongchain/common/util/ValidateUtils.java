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

import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.ValidateException;

import java.util.Collection;

/**
 * 验证工具类
 *
 * @author zhouhui
 * @date 2018/4/25
 * @company Dingxuan
 */
public class ValidateUtils {
    /**
     * 确保对象不为空，否则抛出异常
     * 举例：ValidateUtils.isNotNull(envelope, "envelope can not be null");
     *
     * @param obj
     * @param errorMessage
     * @throws ValidateException
     */
    public static void isNotNull(Object obj, String errorMessage) throws ValidateException {
        if (obj == null) {
            throw new ValidateException(errorMessage);
        }
    }

    /**
     * 确保字符串不为空，否则抛出异常
     * 举例：ValidateUtils.isNotBlank(broker, "broker can not be empty");
     *
     * @param str
     * @param errorMessage
     * @throws ValidateException
     */
    public static void isNotBlank(String str, String errorMessage) throws ValidateException {
        if (StringUtils.isBlank(str)) {
            throw new ValidateException(errorMessage);
        }
    }

    /**
     * 确保对象不为空，否则抛出异常
     * 举例：ValidateUtils.isNotEmpty(list, "list can not be empty");
     *
     * @param collection
     * @param errorMessage
     * @throws ValidateException
     */
    public static void isNotEmpty(Collection collection, String errorMessage) throws ValidateException {
        if (collection == null || collection.size() <= 0) {
            throw new ValidateException(errorMessage);
        }
    }
}
