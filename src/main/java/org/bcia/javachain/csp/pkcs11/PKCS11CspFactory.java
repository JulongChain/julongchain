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
package org.bcia.javachain.csp.pkcs11;

import org.bcia.javachain.csp.factory.ICspFactory;
import org.bcia.javachain.csp.factory.IFactoryOpts;
import org.bcia.javachain.csp.intfs.ICsp;

/**
 * Class description
 *
 * @author
 * @date 4/19/18
 * @company FEITIAN
 */
public class PKCS11CspFactory implements ICspFactory {

    @Override
    public String getName(){return IFactoryOpts.PROVIDER_PKCS11;}


    @Override
    public ICsp getCsp(IFactoryOpts opts){
        IPKCS11FactoryOpts PKCS11Opts=(IPKCS11FactoryOpts) opts;
        PKCS11Csp pkcs11Csp =new PKCS11Csp(PKCS11Opts);
        return pkcs11Csp;
    }
}
