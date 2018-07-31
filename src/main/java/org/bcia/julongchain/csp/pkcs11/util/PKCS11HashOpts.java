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

package org.bcia.julongchain.csp.pkcs11.util;

import org.bcia.julongchain.csp.intfs.opts.IHashOpts;
import org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant;
import sun.security.pkcs11.wrapper.PKCS11Constants;

/**
 * 类描述
 *
 * @author xuying
 * @date 2018/05/21
 * @company FEITIAN
 */
public class PKCS11HashOpts {
    public static class SHA256Opts implements IHashOpts {
        public String getAlgorithm() {
            return PKCS11CSPConstant.SHA256;
        }

        public long getMechanism() {
            return PKCS11Constants.CKM_SHA256;
        }
    }

    public static class SHA384Opts implements IHashOpts{
        public String getAlgorithm() {
            return PKCS11CSPConstant.SHA384;
        }

        public long getMechanism() {
            return PKCS11Constants.CKM_SHA384;
        }
    }

    public static class SHA3_256Opts  implements IHashOpts{
        public String getAlgorithm() {
            return PKCS11CSPConstant.SHA3_256;
        }

		/*
		public long getMechanism() {
			return 0;
		}
		*/
    }

    public static class SHA3_384Opts  implements IHashOpts{
        public String getAlgorithm() {
            return PKCS11CSPConstant.SHA3_384;
        }
    }

    public static class SHA1Opts implements IHashOpts{
        public String getAlgorithm() {
            return PKCS11CSPConstant.SHA1;
        }

        public long getMechanism() {
            return PKCS11Constants.CKM_SHA_1;
        }
    }

    public static class MD5Opts implements IHashOpts{
        public String getAlgorithm() {
            return PKCS11CSPConstant.MD5;
        }

        public long getMechanism() {
            return PKCS11Constants.CKM_MD5;
        }
    }

    public static class MD2Opts implements IHashOpts{
        public String getAlgorithm() {
            return PKCS11CSPConstant.MD2;
        }

        public long getMechanism() {
            return PKCS11Constants.CKM_MD2;
        }
    }
}
