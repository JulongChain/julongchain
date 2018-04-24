/**
 * Copyright SDT. All Rights Reserved.
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
package org.bcia.javachain.csp.gm.sdt;

import org.bcia.javachain.csp.intfs.opts.IRngOpts;
import java.util.Random;
/**
 * Generate Random Data
 *
 * @author tengxiumin
 * @date 4/23/18
 * @company SDT
 */
public class RandomGen {
    public byte[] rng(int len, IRngOpts opts) {

        //判断随机数长度是否为非负整数
        if(len <= 0) {
            return null;
        }
        byte[] result = new byte[len];
        //TODO 调用JNI接口（目前使用系统函数生成随机数）
        result = genHexRandom(len);

        return result;
    }

    private byte[] genHexRandom(int len)  {
        byte[] random = new byte[len];

        for(int i=0; i<len; i++) {
            random[i] = (byte) (new Random().nextInt(16));
        }

        return random;

    }
}
