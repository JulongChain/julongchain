package org.bcia.javachain.bccsp.gm;

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

import org.bcia.javachain.bccsp.factory.IBccspFactory;
import org.bcia.javachain.bccsp.factory.IFactoryOpts;
import org.bcia.javachain.bccsp.intfs.IBccsp;

/**
 * @author zhanglin
 * @purpose Define the class, GmBccspFactory
 * @date 2018-01-25
 * @company Dingxuan
 */

public class GmBccspFactory implements IBccspFactory {

    public GmBccspFactory(){

    }

    public String getName(){
        return IFactoryOpts.PROVIDER_GM;
    }

    public IBccsp getBccsp(IFactoryOpts opts){
        IGmFactoryOpts gmOpts=(IGmFactoryOpts) opts;
        GmBccsp gmBccsp=new GmBccsp(gmOpts);
        return gmBccsp;
    }
}
