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
package org.bcia.julongchain.csp.gmt0016.excelsecu.bean;

import org.bcia.julongchain.csp.gmt0016.excelsecu.security.ECCPublicKeyBlob;
import org.bcia.julongchain.csp.gmt0016.excelsecu.security.RSAPublicKeyBlob;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class PublicKeyBlob {
    public static final int RSA_PUBLIC_KEY_BLOB_TYPE = 0;
    public static final int ECC_PUBLIC_KEY_BLOB_TYPE = 1;
    private int type = -1;
    private long blobLen;
    private RSAPublicKeyBlob mRSAPublicKeyBlob;
    private ECCPublicKeyBlob mECCPublicKeyBlob;

    public int getType()
    {
        return type;
    }

    public long getBlobLen()
    {
        return blobLen;
    }

    public RSAPublicKeyBlob getRSAPublicKeyBlob()
    {
        return mRSAPublicKeyBlob;
    }

    public ECCPublicKeyBlob getECCPublicKeyBlob()
    {
        return mECCPublicKeyBlob;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public void setBlobLen(long blobLen)
    {
        this.blobLen = blobLen;
    }

    public void setRSAPublicKeyBlob(RSAPublicKeyBlob rsaPublicKeyBlob)
    {
        this.mRSAPublicKeyBlob = rsaPublicKeyBlob;
    }

    public void setECCPublicKeyBlob(ECCPublicKeyBlob eccPublicKeyBlob)
    {
        this.mECCPublicKeyBlob = eccPublicKeyBlob;
    }
}
