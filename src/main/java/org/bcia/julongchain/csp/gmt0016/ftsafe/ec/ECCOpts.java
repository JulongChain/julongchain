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
package org.bcia.julongchain.csp.gmt0016.ftsafe.ec;

import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant;
import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyImportOpts;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class ECCOpts {

    public static class ECCKeyGenOpts implements IKeyGenOpts {

        private boolean bTemporary;

        public ECCKeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM2;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }


    public static class ECCKeyImportOpts implements IKeyImportOpts {
        private boolean bTemporary;
        private long lAlgID;

        public ECCKeyImportOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM2;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        public void setAlgID(long algid) {
            this.lAlgID = algid;
        }

        public long getAlgID() {
            return lAlgID;
        }
    }
}
