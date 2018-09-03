/**
 * Copyright BCIA. All Rights Reserved.
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
package org.bcia.julongchain.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JulongChain日志工厂
 *
 * @author zhouhui
 * @date 2018/3/29
 * @company Dingxuan
 */
public class JulongChainLogFactory {
    public static JulongChainLog getLog(Class<?> clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);

        JulongChainLog log = new JulongChainLog();
        log.setLogger(logger);
        return log;
    }

    public static JulongChainLog getLog(String name) {
        Logger logger = LoggerFactory.getLogger(name);

        JulongChainLog log = new JulongChainLog();
        log.setLogger(logger);
        return log;
    }

    public static JulongChainLog getLog() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();

//        for (int i = 0; i < sts.length; i++) {
//            System.out.print("sts[" + i + "].getClassName()-----$" + sts[i].getClassName()+"\r\n");
//        }

        Logger logger = LoggerFactory.getLogger(sts[2].getClassName());

        JulongChainLog log = new JulongChainLog();
        log.setLogger(logger);
        return log;
    }
}
