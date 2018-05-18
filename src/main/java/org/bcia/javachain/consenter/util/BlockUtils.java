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
package org.bcia.javachain.consenter.util;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author zhangmingyang
 * @Date: 2018/5/11
 * @company Dingxuan
 */
public class BlockUtils {


    public static void main(String[] args) {
        String[][] first = {};
        String[] second ={"678", "910"};
        String[][] both = (String[][]) ArrayUtils.add(first, second);

    }
}
