/**
 * Copyright DingXuan. 2017 All Rights Reserved.
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
package org.bcia.javachain.peer.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Peer节点命令行
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public class PeerCLI extends CommandLine {

    /**
     * Add left-over unrecognized option/argument.
     *
     * @param arg the unrecognized option/argument.
     */
    public void addArg(String arg)
    {
        super.addArg(arg);
    }

    /**
     * Add an option to the command line.  The values of the option are stored.
     *
     * @param opt the processed option
     */
    public void addOption(Option opt)
    {
        super.addOption(opt);
    }
}
