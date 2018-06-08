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
package org.bcia.julongchain.core.container;

import org.bcia.julongchain.core.RWMutex;
import org.bcia.julongchain.core.container.api.VM;
import org.bcia.julongchain.core.container.dockercontroller.DockerVM;
import org.bcia.julongchain.core.container.inproccontroller.InprocVM;

import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/2
 * @company Dingxuan
 */
public class VMController extends RWMutex{

    public static final String DOCKER = "Docker";
    public static final String SYSTEM = "System";

    private Map<String, RefCountedLock> containerLocks;

    public Map<String, RefCountedLock> getContainerLocks() {
        return containerLocks;
    }

    public void setContainerLock(Map<String, RefCountedLock> containerLocks) {
        this.containerLocks = containerLocks;
    }

    public VM newVM(String type) {

        VM vm = null;

        switch (type) {
            case DOCKER:
                vm = new DockerVM();
                break;
            case SYSTEM:
                vm = new InprocVM();
                break;
            default:
                vm = new DockerVM();
                break;
        }

        return vm;
    }

    public void lockContainer(String id) {

    }

    public void unlockContainer(String id) {

    }

}
