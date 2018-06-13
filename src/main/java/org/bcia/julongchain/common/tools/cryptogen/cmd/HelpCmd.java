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
 * @author chenhao
 * @date 2018/5/21
 * @company Excelsecu
 */
public class HelpCmd implements ICryptoGenCmd  {

    @Override
    public void execCmd(String[] args) throws JavaChainException {
        System.out.println("Utility for generating BCIA key material\n" +
                "Flags:\n" +
                "  --help  Show context-sensitive help. e.g. generate --help\n" +
                "Commands:\n" +
                "  help\n" +
                "    Show help\n" +
                "  generate [opts]\n" +
                "    Generate key material\n" +
                "  extend\n" +
                "    Extend existing network\n" +
                "  showtemplate\n" +
                "    Show the default configuration template\n" +
                "  version\n" +
                "    Show version information");
    }
}
