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

import org.bcia.julongchain.core.container.ccintf.SCID;
import org.bcia.julongchain.core.container.ccintf.ICCSupport;
import org.bcia.julongchain.core.container.api.IBuildSpecFactory;
import org.bcia.julongchain.core.container.api.IFormat;
import org.bcia.julongchain.core.container.api.IPrelaunchFunc;
import org.bcia.julongchain.core.container.api.VM;

import javax.naming.Context;
import java.io.Reader;
import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/2
 * @company Dingxuan
 */
public class InprocVM implements VM {

    private String id;

    @Override
    public void deploy(Context ctxt, SCID ccid, String[] args, String[] env,
                       Reader reader) {

    }

    @Override
    public void start(Context ctxt, SCID ccid, String[] args, String[] env,
                      Map<String, byte[]> filesToUpload, IBuildSpecFactory
                                  build, IPrelaunchFunc preLaunchFunc) {

    }

    @Override
    public void stop(Context ctxt, SCID ccid, Long timeout, Boolean dontkill,
                     Boolean dontremove) {

    }

    @Override
    public void destroy(Context ctxt, SCID ccid, Boolean force, Boolean
            noprune) {

    }

    @Override
    public String getVMName(SCID ccID, IFormat format) {
        return null;
    }

    public InprocVM getInstance(Context context, InprocContainer
            ipctemplate, String instName, String[] args, String[] env) {
        return null;
    }

    public void launchInProc(Context context, String id, String[] args,
                             String[] env, ICCSupport iccSupport) {

    }
}
