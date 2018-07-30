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

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.SarException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.csp.gmt0016.ftsafe.IGMT0016FactoryOpts;

import static org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant.*;


/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class GMDigest {

    // length of the digest in bytes
    // private static int digestLength;
    private static long lAlgID;
    private static JavaChainLog logger;

    public GMDigest(String alg) {

        if(alg == SM3)
        {
            this.lAlgID = SGD_SM3;
            //this.digestLength = 32;
        }
        if(alg == SHA1)
        {
            this.lAlgID = SGD_SHA1;
            //this.digestLength = 20;
        }
        if(alg == SHA256)
        {
            this.lAlgID = SGD_SHA256;
            //this.digestLength = 32;
        }

    }

    public static byte[] getHash(byte[] msg, IGMT0016FactoryOpts opts) throws JavaChainException {
        try {
            long lHashHandle = opts.getSKFFactory().SKF_DigestInit(opts.getDevHandle(), lAlgID, null, null);
            byte[] hashData = opts.getSKFFactory().SKF_Digest(lHashHandle, msg, msg.length);
            return  hashData ;
        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
        }
    }

}
