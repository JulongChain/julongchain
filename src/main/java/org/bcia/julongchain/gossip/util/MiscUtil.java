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
package org.bcia.julongchain.gossip.util;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * class description
 *
 * @author
 * @date 18-7-25
 * @company Dingxuan
 */
public class MiscUtil {

    public static Integer getIntOrDefault(String key, Integer defVal) {
        String env = System.getenv(key);
        if (StringUtils.isEmpty(env) || NumberUtils.isNumber(env)) {
            return defVal;
        }
        return Integer.parseInt(env);
    }

}
