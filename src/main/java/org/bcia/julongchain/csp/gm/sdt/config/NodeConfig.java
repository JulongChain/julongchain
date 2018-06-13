/**
 * Copyright SDT. All Rights Reserved.
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
package org.bcia.julongchain.csp.gm.sdt.config;

/**
 * 配置信息
 *
 * @author tengxiumin
 * @date 2018/05/17
 * @company SDT
 */
public class NodeConfig {

    private CspConfig csp;
    private String mspConfigPath;
    private String localMspId;
    private String localMspType;

    public CspConfig getCsp() {
        return csp;
    }

    public void setCsp(CspConfig csp) {
        this.csp = csp;
    }

    public String getMspConfigPath() {
        return mspConfigPath;
    }

    public void setMspConfigPath(String mspConfigPath) {
        this.mspConfigPath = mspConfigPath;
    }

    public String getLocalMspId() {
        return localMspId;
    }

    public void setLocalMspId(String localMspId) {
        this.localMspId = localMspId;
    }

    public String getLocalMspType() {
        return localMspType;
    }

    public void setLocalMspType(String localMspType) {
        this.localMspType = localMspType;
    }
}
