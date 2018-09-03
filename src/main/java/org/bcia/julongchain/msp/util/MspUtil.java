/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.msp.util;

import org.bouncycastle.asn1.x509.Certificate;

import java.util.HashMap;
import java.util.Map;

/**
 * msp工具类
 * @author zhangmingyang
 * @date 2018/07/04
 * @company Dingxuan
 */
public class MspUtil {
    public static Map parseFromString(String str) {
        String[] split = str.split(",");
        Map<String, String> subjectMap = new HashMap<>();
        for (int i = 0; i < split.length; i++) {
            String[] element = split[i].split("=");
            if (element.length == 1) {
                subjectMap.put(element[0].toString(), "");
            }
            if (element.length == 2) {
                subjectMap.put(element[0].toString(), element[1].toString());
            }
        }
        return subjectMap;
    }
}
