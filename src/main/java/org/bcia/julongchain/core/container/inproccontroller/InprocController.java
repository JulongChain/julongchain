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
package org.bcia.julongchain.core.container.inproccontroller;

import org.bcia.julongchain.common.exception.SmartContractException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ssc.ISystemSmartContract;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunianle
 * @date @date 2018/05/17
 * @company Dingxuan
 */
@Component
public class InprocController {
    private JavaChainLog log = JavaChainLogFactory.getLog(InprocContainer.class);

    private Map<String,InprocContainer> typeRegistry=new HashMap<String,InprocContainer>();
    private Map<String,InprocContainer>  instRegistry=new HashMap<String,InprocContainer>();

    public void register(String sscPath, ISystemSmartContract contract) throws SmartContractException{
        InprocContainer tmp=typeRegistry.get(sscPath);
        if(tmp!=null){
             String msg=String.format("%s already registered",sscPath);
             throw new SmartContractException(msg);
        }
        InprocContainer container=new InprocContainer(contract);
        typeRegistry.put(sscPath,container);
    }

    public Map<String, InprocContainer> getTypeRegistry() {
        return typeRegistry;
    }

    public Map<String, InprocContainer> getInstRegistry() {
        return instRegistry;
    }
}
