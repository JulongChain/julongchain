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
package org.bcia.julongchain.common.exception;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class JCSKFException extends JavaChainException {

    public static final long JC_SKF_OK = 0x00000000;
    public static final long JC_SKF_NODEV = 0x80000001;
    public static final long JC_SKF_NOAPP = 0x80000002;
    public static final long JC_SKF_NOCONTAINER = 0x80000003;


    private static final String MODULE_NAME = "Gmt0016 FTsafe ";
    private long errCode;

    public JCSKFException(long errCode) {
        super(MODULE_NAME + String.format("Error code: 0x%08x", errCode));
        this.errCode = errCode;
    }

    public long getErrCode() {
        return errCode;
    }
}
