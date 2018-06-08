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
package org.bcia.julongchain.core.container.dockercontroller;

import org.bcia.julongchain.core.container.ccintf.SCID;
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
public class DockerVM implements VM{

    private String id;

    public IDockerClient getDockerClient() {
        return new IDockerClient() {
            @Override
            public String createContainer(String opts) {
                return null;
            }

            @Override
            public void uploadToContainer(String id, String opts) {

            }

            @Override
            public void startContainer(String id, String cfg) {

            }

            @Override
            public void attachToContainer(String opts) {

            }

            @Override
            public void buildImage(String opts) {

            }

            @Override
            public void removeImageExtended(String id, String opts) {

            }

            @Override
            public void stopContainer(String id, Long timeout) {

            }

            @Override
            public void KillContainer(String opts) {

            }

            @Override
            public void removeContainer(String opts) {

            }
        };
    }

    public void createContainer(Context ctxt, IDockerClient client, String
            imageID, String containerID, String[] args, String[] env, Boolean
                                        attachStdout) {

    }

    public void deployImage(IDockerClient client, SCID ccid, String[] args,
                            String[] env, Reader reader) {

    }

    @Override
    public void start(Context context, SCID ccid, String[] args, String[]
            env, Map<String, byte[]> filesToUpload, IBuildSpecFactory
            builder, IPrelaunchFunc prelaunchFunc) {

    }

    @Override
    public void stop(Context context, SCID ccid, Long timeout, Boolean
            dontkill, Boolean dontremove) {

    }

    public void stopInternal(Context context, IDockerClient dockerClient,
                             String id, Long timeout, Boolean dontkill,
                             Boolean dontremove) {

    }

    @Override
    public void destroy(Context context, SCID ccid, Boolean force, Boolean
            noprune) {

    }

    @Override
    public String getVMName(SCID ccid, IFormat format) {
        return "";
    }

    public String formatImageName(String name) {
        return "";
    }

    @Override
    public void deploy(Context ctxt, SCID ccid, String[] args, String[] env, Reader reader) {

    }
}
