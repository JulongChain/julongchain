package org.bcia.javachain.bccsp.factory;

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

import org.bcia.javachain.bccsp.intfs.IBccsp;

/**
 * @author zhanglin
 * @purpose Define the interface, IBccspFactory
 * @date 2018-01-25
 * @company Dingxuan
 */

// IBccspFactory is a factory for blockchain cryptographic service.
public interface IBccspFactory {

    // The getName returns the name of this factory.
    String getName();

    // The getBccsp returns an instance of Bccsp according to the opts whose type is IFactoryOpts.
    IBccsp getBccsp(IFactoryOpts opts);
}
