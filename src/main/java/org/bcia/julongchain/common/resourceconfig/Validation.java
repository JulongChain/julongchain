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
package org.bcia.julongchain.common.resourceconfig;

/**
 * Validation returns how to validate transactions for this chaincode.
 * The string returned is the name of the validation method (usually 'vssc')
 * and the bytes returned are the argument to the validation (in the case of
 * 'vssc', this is a marshaled pb.VSCCArgs message).
 *
 * @author wanliangbing
 * @date 2018/3/15
 * @company Dingxuan
 */
public class Validation {

    private String method;
    private byte[] args;

    public Validation() {
    }

    public Validation(String method, byte[] args) {
        this.method = method;
        this.args = args;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public byte[] getArgs() {
        return args;
    }

    public void setArgs(byte[] args) {
        this.args = args;
    }
}
