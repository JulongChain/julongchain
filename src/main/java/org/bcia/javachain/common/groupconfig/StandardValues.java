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
package org.bcia.javachain.common.groupconfig;

import com.google.protobuf.Message;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.common.Configuration;
import org.bcia.javachain.protos.msp.MspConfigPackage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 标准值对象(该对象仅供辅助读取配置对象，可能后面可以去掉)
 *
 * @author zhouhui
 * @date 2018/3/28
 * @company Dingxuan
 */
public class StandardValues {
    private Map<String, Message> lookup;

    public StandardValues() {
        lookup = new HashMap<String, Message>();
    }

    public StandardValues(Message... msgs) {
        lookup = new HashMap<String, Message>();

        if (msgs != null) {
            for (Message msg : msgs) {
                add(msg);
            }
        }
    }

    /**
     * 增加消息对象
     *
     * @param msg
     */
    private void add(Message msg) {
        lookup.put(msg.getClass().getSimpleName(), msg);
    }

//    private void put(String key, Message msg) {
//        lookup.put(key, msg);
//    }
//
//    public static StandardValues valuesFromTree(Configtx.ConfigTree configTree, Class... classes) {
//        if (configTree == null || configTree.getValuesMap() == null) {
//            return null;
//        }
//
//        StandardValues standardValues = new StandardValues();
//
//        Iterator<Map.Entry<String, Configtx.ConfigValue>> entries = configTree.getValuesMap().entrySet().iterator();
//        while (entries.hasNext()) {
//            Map.Entry<String, Configtx.ConfigValue> entry = entries.next();
//            standardValues.put(entry.getKey(), entry.getValue().getValue());
//        }
//
////        Configuration.Capabilities capabilities = Configuration.Capabilities.newBuilder().build();
////        MspConfigPackage.MSPConfig mspConfig = MspConfigPackage.MSPConfig.newBuilder().build();
////
////        Message[] msgs = new Message[]{capabilities, mspConfig};
////
////        StandardValues standardValues = new StandardValues(msgs);
//
//        return standardValues;
//    }


}
