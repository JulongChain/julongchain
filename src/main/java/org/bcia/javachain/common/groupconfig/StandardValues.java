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
import org.bcia.javachain.core.node.NodeConfig;
import org.bcia.javachain.protos.common.Configuration;
import org.bcia.javachain.protos.msp.MspConfigPackage;
import org.bcia.javachain.protos.peer.Resources;

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
public class StandardValues {
    private Map<String, Message> lookup;

    public StandardValues(Message[] msgs) {
        lookup = new HashMap<String, Message>();

        for (Message msg : msgs) {
            initialize(msg);
        }

        Iterator<Map.Entry<String, Message>> entries = lookup.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry<String, Message> entry = entries.next();

            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

        }
    }

    private void initialize(Message msg) {
        lookup.put(msg.getClass().getName(), msg);
//        if(msg instanceof Configuration.Capabilities){
//            lookup.put("Capabilities", msg);
//        }
//
//        if(msg instanceof org.bcia.javachain.protos.node.Configuration.AnchorNodes){
//            lookup.put("Capabilities", msg);
//        }
//
//        if(msg instanceof MspConfigPackage.MSPConfig){
//            lookup.put("MSP", msg);
//        }
//
//        if(msg instanceof Resources.SmartContractIdentifier){
//            lookup.put(msg.getClass().getName(), msg);
//        }
//
//        if(msg instanceof Resources.SmartContractValidation){
//            lookup.put("SmartContractValidation", msg);
//        }
//
//        if(msg instanceof Resources.SmartContractEndorsement){
//            lookup.put("SmartContractEndorsement", msg);
//        }
    }

    public static StandardValues valuesFromChild() {

        Configuration.Capabilities capabilities = Configuration.Capabilities.newBuilder().build();
        MspConfigPackage.MSPConfig mspConfig = MspConfigPackage.MSPConfig.newBuilder().build();

        Message[] msgs = new Message[]{capabilities, mspConfig};

        StandardValues standardValues = new StandardValues(msgs);

        return standardValues;

    }


}
