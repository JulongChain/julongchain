/**
 * Copyright Feitian. All Rights Reserved.
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
package org.bcia.julongchain.csp.pkcs11;

import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class description
 *
 * @author Ying Xu
 * @date 7/18/18
 * @company FEITIAN
 */
public class PKCS11CspLog {

    private static JulongChainLog logger;

    public void setLogMsg(String msg, int level, Class<?> clazz) {

        logger = JulongChainLogFactory.getLog(clazz);

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        String str = df.format(date);

        StringBuilder value = new StringBuilder();
        value.append(str);
        if (level == 0) {
            value.append(" [CSP] [DEBUG] ");
            value.append(msg);
            logger.debug(value.toString());
        }else if(level == 1) {
            value.append(" [CSP] [INFO] ");
            value.append(msg);
            logger.info(value.toString());
        }else if(level == 2) {
            value.append(" [CSP] [ERROR] ");
            value.append(msg);
            logger.error(value.toString());
        }

    }
}
