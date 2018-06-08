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
package org.bcia.julongchain.consenter.common.cmd.factory;

import org.bcia.julongchain.consenter.common.cmd.IConsenterCmd;
import org.bcia.julongchain.consenter.common.cmd.impl.BenchMarkCmd;
import org.bcia.julongchain.consenter.common.cmd.impl.StartCmd;
import org.bcia.julongchain.consenter.common.cmd.impl.VersionCmd;
import org.bcia.julongchain.consenter.util.Constant;


/**
 * @author zhangmingyang
 * @Date: 2018/3/1
 * @company Dingxuan
 */
public class ConsenterCmdFactory {
    public static IConsenterCmd getInstance(String command){
    if(Constant.VERSION.equalsIgnoreCase(command)){
        return new VersionCmd();
    }else if(Constant.START.equalsIgnoreCase(command)){
        return new StartCmd();
    }else if(Constant.BENCHMARK.equalsIgnoreCase(command)){
        return new BenchMarkCmd();
    }
        return null;
    }

}
