/**
 * Copyright Feitian. All Rights Reserved.
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
package org.bcia.julongchain.csp.gmt0016.ftsafe.rsa;

import org.bcia.julongchain.csp.gmt0016.ftsafe.IGMT0016KeyImportOpts;

import static org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant.*;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public enum RSAImportOpts implements IGMT0016KeyImportOpts {

    SM1_ECB_Ephemeral("SM1_ECB", SGD_SM1_ECB, true, 1),
    SM1_ECB("SM1_ECB", SGD_SM1_ECB, false, 2),
    SM1_CBC_Ephemeral("SM1", SGD_SM1_CBC, true, 3),
    SM1_CBC("SM1", SGD_SM1_CBC, false, 4);

    private String salg;
    private long algid;
    private int index;
    private boolean ephemeral;

    private RSAImportOpts(String salg, long algid, boolean ephemeral, int index) {
        this.salg = salg;
        this.algid = algid;
        this.ephemeral = ephemeral;
        this.index = index;
    }

    @Override
    public String getAlgorithm() {
        return salg;
    }

    @Override
    public boolean isEphemeral() {
        return ephemeral;
    }

    @Override
    public long getAlgID() {
        return algid;
    }

    @Override
    public int getIndex() {
        return index;
    }
}
