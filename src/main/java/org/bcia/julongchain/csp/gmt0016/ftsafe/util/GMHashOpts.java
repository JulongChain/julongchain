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
import org.bcia.julongchain.csp.intfs.opts.IHashOpts;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class GMHashOpts {

    public static class SM3SignPreOpts implements IGMHashOpts{

        private byte[] ski;
        private String pubID;

        @Override
        public void setSki(byte[] ski) {
            this.ski = ski;
        }

        public void setPubID(String pubid) {
            this.pubID = pubid;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM3;
        }

        @Override
        public long getMechanism() {
            return GMT0016CspConstant.SGD_SM3;
        }

        @Override
        public byte[] getSki() {
            return ski;
        }

        @Override
        public String getPubID() {
            return pubID;
        }
    }

    public static class SM3HashOpts implements IHashOpts{

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM3;
        }
    }

    public static class SHA1HashOpts implements IHashOpts {
        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SHA1;
        }

    }

    public static class SHA256HashOpts implements IHashOpts{
        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SHA256;
        }
    }
}
