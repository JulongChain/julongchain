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

/**
 * @author chenhao, liuxifeng
 * @date 2018/4/16
 * @company Excelsecu
 */
public class CryptoGenCmdFactory {

    public static ICryptoGenCmd getInstance(String command) {

        if (CmdConstant.VERSION.equalsIgnoreCase(command)) {
            return new VersionCmd();
        } else if (CmdConstant.SHOW_TEMPLATE.equalsIgnoreCase(command)) {
            return new ShowTemplateCmd();
        } else if (CmdConstant.GENERATE.equalsIgnoreCase(command)) {
            return new GenerateCmd();
        } else if (CmdConstant.EXTEND.equalsIgnoreCase(command)) {
            return new ExtendCmd();
        } else if (CmdConstant.HELP.equalsIgnoreCase(command)) {
            return new HelpCmd();
        } else if (CmdConstant.HELP_OPT.equalsIgnoreCase(command)) {
            return new HelpCmd();
        }
        return null;
    }
}
