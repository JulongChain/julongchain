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
package org.bcia.julongchain.csp.gmt0016.ftsafe.symmetry;

import org.bcia.julongchain.csp.intfs.IKey;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class SymmCspKey implements IKey{

    private byte[] privkey;
    private boolean exportable;
    private long handle;

    public SymmCspKey(byte[] privkey, boolean exportable, long handle) {
        this.privkey = new byte[privkey.length];
        System.arraycopy(privkey, 0, this.privkey, 0, privkey.length);
        this.exportable = !exportable;
        this.handle = handle;
    }

    @Override
    public byte[] toBytes() {
        if(exportable)
        {
            return privkey;
        }
        return null;
    }

    @Override
    public byte[] ski() {
        //1
        return null;
    }

    @Override
    public boolean isSymmetric() {
        return true;
    }

    @Override
    public boolean isPrivate() {
        return true;
    }

    @Override
    public IKey getPublicKey() {
        return null;
    }

    public long getHandle() {
        return handle;
    }
}
