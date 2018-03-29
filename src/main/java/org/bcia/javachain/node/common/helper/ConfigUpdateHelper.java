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
package org.bcia.javachain.node.common.helper;

import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.protos.common.Configtx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/28
 * @company Dingxuan
 */
public class ConfigUpdateHelper {
    public static Configtx.ConfigUpdate compute(Configtx.Config original, Configtx.Config pending) throws
            ValidateException {
        if (original.getGroupChild() == null) {
            throw new ValidateException("No group child found in original");
        }

        if (pending.getGroupChild() == null) {
            throw new ValidateException("No group child found in pending");
        }

        return null;
    }

    private static Object[] computePoliciesMapUpdate(Map<String, Configtx.ConfigPolicy> originalMap, Map<String,
            Configtx.ConfigPolicy> pendingMap) {
        //TODO:如果有空的情况

        Map<String, Configtx.ConfigPolicy> readSet = new HashMap<String, Configtx.ConfigPolicy>();
        Map<String, Configtx.ConfigPolicy> writeSet = new HashMap<String, Configtx.ConfigPolicy>();
        Map<String, Configtx.ConfigPolicy> sameSet = new HashMap<String, Configtx.ConfigPolicy>();


        //先循环originalMap
        Iterator<Map.Entry<String, Configtx.ConfigPolicy>> entries = originalMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Configtx.ConfigPolicy> entry = entries.next();
            String policyName = entry.getKey();
            Configtx.ConfigPolicy originalPolicy = entry.getValue();

            //判断在新集合pendingMap有没有对应项
            Configtx.ConfigPolicy pendingPolicy = pendingMap.get(policyName);
            if (pendingPolicy == null) {
                //TODO；如何处理
            }

            if (pendingPolicy.getModPolicy().equals(originalPolicy.getModPolicy()) && originalPolicy.getPolicy()
                    .equals(pendingPolicy.getPolicy())) {
                sameSet.put(policyName, originalPolicy);
            }
        }

        return null;


    }
}
