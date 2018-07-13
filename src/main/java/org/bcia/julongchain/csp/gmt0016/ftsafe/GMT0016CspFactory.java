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
package org.bcia.julongchain.csp.gmt0016.ftsafe;

import org.bcia.julongchain.csp.factory.ICspFactory;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.intfs.ICsp;

/**
 * Class description
 *
 * @author
 * @date 7/3/18
 * @company FEITIAN
 */
public class GMT0016CspFactory implements ICspFactory {

    @Override
    public String getName(){return IFactoryOpts.PROVIDER_GMT0016;}


    @Override
    public ICsp getCsp(IFactoryOpts opts){
        IGMT0016FactoryOpts gmopt = (IGMT0016FactoryOpts) opts;
        GMT0016Csp gmcsp = new GMT0016Csp(gmopt);
        return gmcsp;
    }
}
