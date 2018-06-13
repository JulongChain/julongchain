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

package org.bcia.julongchain.common.tools.cryptogen.bean;

import java.util.List;

/**
 * @author chenhao, liuxifeng
 * @date 2018/4/4
 * @company Excelsecu
 */
public class Config {

    private List<OrgSpec> consenterOrgs;

    private List<OrgSpec> peerOrgs;

    public List<OrgSpec> getConsenterOrgs() {
        return consenterOrgs;
    }

    public void setConsenterOrgs(List<OrgSpec> consenterOrgs) {
        this.consenterOrgs = consenterOrgs;
    }

    public List<OrgSpec> getPeerOrgs() {
        return peerOrgs;
    }

    public void setPeerOrgs(List<OrgSpec> peerOrgs) {
        this.peerOrgs = peerOrgs;
    }
}
