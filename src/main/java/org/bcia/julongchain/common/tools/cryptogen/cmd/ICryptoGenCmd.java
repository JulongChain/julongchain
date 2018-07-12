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
package org.bcia.julongchain.common.tools.cryptogen.cmd;

import org.bcia.julongchain.common.exception.JavaChainException;

/**
 * 命令类接口
 *
 * @author chenhao, liuxifeng
 * @date 2018/4/16
 * @company Excelsecu
 */
public interface ICryptoGenCmd {

    /**
     * 执行命令
     * @param args 命令参数
     * @throws JavaChainException
     */
    void execCmd(String[] args) throws JavaChainException;
}
