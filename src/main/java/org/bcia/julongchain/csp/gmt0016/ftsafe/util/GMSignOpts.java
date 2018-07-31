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
package org.bcia.julongchain.csp.gmt0016.ftsafe.util;

import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant;
import org.bcia.julongchain.csp.intfs.opts.ISignerOpts;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public enum GMSignOpts implements ISignerOpts {

    SM3(GMT0016CspConstant.SM3, GMT0016CspConstant.SGD_SM3, 1),
    SHA1(GMT0016CspConstant.SHA1, GMT0016CspConstant.SGD_SHA1, 2),
    SHA256(GMT0016CspConstant.SHA256, GMT0016CspConstant.SGD_SHA256, 3);

    private String alg;
    private int index;
    private long algId;

    private GMSignOpts(String alg, long algid, int index) {
        this.alg = alg;
        this.index = index;
        this.algId = algid;
    }

    @Override
    public String getAlgorithm() {
        return alg;
    }

    @Override
    public String hashFunc() {
        // TODO Auto-generated method stub
        return alg;
    }

}
