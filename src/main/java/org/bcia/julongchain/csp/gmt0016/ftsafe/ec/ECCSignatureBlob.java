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

import static org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant.*;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class ECCSignatureBlob {

    private byte[] r;
    private byte[] s;

    public ECCSignatureBlob() {
        this.r = new byte[(int)ECC_MAX_XCOORDINATE_BITS_LEN/8];
        this.s = new byte[(int)ECC_MAX_YCOORDINATE_BITS_LEN/8];
    }

    public ECCSignatureBlob(byte[] r, byte[] s) {
        this.r = r;
        this.s = s;
    }


    public byte[] getR() {
        return r;
    }

    public byte[] getS() {
        return s;
    }

    public void setR(byte[] r) {
        this.r = r;
    }

    public void setS(byte[] s) {
        this.s = s;
    }
}
