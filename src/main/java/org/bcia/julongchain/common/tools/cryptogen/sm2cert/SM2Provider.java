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
package org.bcia.julongchain.common.tools.cryptogen.sm2cert;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;

/**
 * @author chenhao
 * @date 2018/4/18
 * @company Excelsecu
 */
public class SM2Provider extends Provider {

    @SuppressWarnings("Convert2Lambda")
    public SM2Provider() {
        super("ExcelsecuSM2", 1, "Excelsecu SM2 Provider");

        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                Service service = new Service(
                        SM2Provider.this, "AlgorithmParameters", "SM2",
                        SM2Parameters.class.getName(), null, null);
                putService(service);
                return null;
            }
        });
    }
}
