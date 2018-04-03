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
package org.bcia.javachain.core.container.api;

import org.bcia.javachain.core.container.ccintf.CCID;

import javax.naming.Context;
import java.io.Reader;
import java.util.Map;

/**
 * VM is an abstract virtual image for supporting arbitrary virual machines
 *
 * @author wanliangbing
 * @date 2018/4/2
 * @company Dingxuan
 */
public interface VM {

    void deploy(Context ctxt, CCID ccid, String[] args, String[] env, Reader
            reader);

    void start(Context ctxt, CCID ccid, String[] args, String[] env,
               Map<String, byte[]> filesToUpload, IBuildSpecFactory build,
               IPrelaunchFunc preLaunchFunc);

    void stop(Context ctxt, CCID ccid, Long timeout, Boolean dontkill,
              Boolean dontremove);

    void destroy(Context ctxt, CCID ccid, Boolean force, Boolean noprune);

    String getVMName(CCID ccID, IFormat format);

}
