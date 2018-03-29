package org.bcia.javachain.csp.factory;

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

/**
 * @author zhanglin,sunianle
 * @purpose Define the interface, IFactoryOpts
 * @date 2018-01-25
 * @company Dingxuan
 */

// The IFactoryOpts contains options for factories.
public interface IFactoryOpts {

    // The values of string type below present providers' names to be implemented.
    String PROVIDER_NIST = "NIST";
    String PROVIDER_GM = "GM";
    String PROVIDER_GM_BOUNCYCASTLE = "GM_BOUNCYCASTLE";
    String PROVIDER_PKCS11 = "PKCS11";
    String PROVIDER_GMT0016 = "GMT0016";
    String PROVIDER_GMT0018 = "GMT0018";
    String PROVIDER_GMT0019 = "GMT0019";

    // The getProviderName returns a provider's name.
    String getProviderName();

    // The getProviderDescription returns the description of a provider.
    String getProviderDescription();

    boolean isDefaultCsp();

}
