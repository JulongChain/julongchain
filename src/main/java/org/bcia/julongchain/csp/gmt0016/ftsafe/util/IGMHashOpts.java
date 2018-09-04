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

import org.bcia.julongchain.csp.intfs.opts.IHashOpts;

/**
 * Interface description
 *
 * @author Ying Xu
 * @date 7/4/18
 * @company FEITIAN
 */
public interface IGMHashOpts extends IHashOpts {

    public long getMechanism();

    public byte[] getSki();

    public String getPubID();

    public void setSki(byte[] ski);

    public void setPubID(String pubid);
}
