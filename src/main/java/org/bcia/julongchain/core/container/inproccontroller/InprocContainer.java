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

import org.bcia.julongchain.core.smartcontract.shim.ISmartContract;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/7/18
 * @company Dingxuan
 */
public class InprocContainer {

    private ISmartContract smartContract;
    private Boolean running;
    private String[] args;
    private String[] env;
    private Chan stopChan;

    public InprocContainer(ISmartContract smartContract) {
        this.smartContract = smartContract;
    }

    public ISmartContract getSmartContract() {
        return smartContract;
    }

    public void setSmartContract(ISmartContract smartContract) {
        this.smartContract = smartContract;
    }

    public Boolean getRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String[] getEnv() {
        return env;
    }

    public void setEnv(String[] env) {
        this.env = env;
    }

    public Chan getStopChan() {
        return stopChan;
    }

    public void setStopChan(Chan stopChan) {
        this.stopChan = stopChan;
    }
}
