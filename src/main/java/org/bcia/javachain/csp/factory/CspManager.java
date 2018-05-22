/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.javachain.csp.factory;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.csp.gm.dxct.GmCspFactory;
import org.bcia.javachain.csp.gm.dxct.GmFactoryOpts;
import org.bcia.javachain.csp.intfs.ICsp;

import java.util.HashMap;
import java.util.List;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/29/18
 * @company Dingxuan
 */
public class CspManager {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CspManager.class);
    //default csp
    private static ICsp defaultCsp=null;
    //initFactories尚未调用之前，临时使用的csp
    private static ICsp bootCsp=null;
    //csp factory map
    private static HashMap<String,ICsp> cspMap=new HashMap<String,ICsp>();

    /**
     * 读取配置文件，并对工厂做初始化，创建相应的Csp
     * @param optsList 工厂配置列表
     */
    public static void initCspFactories(List<IFactoryOpts> optsList){
        for(IFactoryOpts opts:optsList){
            String providerName=opts.getProviderName();
            if(providerName==IFactoryOpts.PROVIDER_GM){
                GmCspFactory factory=new GmCspFactory();
                initCsp(factory,opts);
            }
            else if(providerName==IFactoryOpts.PROVIDER_GM_BOUNCYCASTLE){

            }
            else if(providerName==IFactoryOpts.PROVIDER_GMT0016){

            }
            else if(providerName==IFactoryOpts.PROVIDER_GMT0018){

            }
            else if(providerName==IFactoryOpts.PROVIDER_GMT0019){

            }
            else if(providerName==IFactoryOpts.PROVIDER_NIST){

            }
            else if(providerName==IFactoryOpts.PROVIDER_PKCS11){

            }
        }
    }
    //根据工厂创建CSP实例，并做初始化
    private static void initCsp(ICspFactory factory,IFactoryOpts opts){
        ICsp csp = factory.getCsp(opts);
        log.info("Initialize CSP {}",opts.getProviderName());
        cspMap.put(opts.getProviderName(),csp);
        if(opts.isDefaultCsp()){
            defaultCsp=csp;
        }
    }

    //返回默认的CSP
    public static ICsp getDefaultCsp(){
        if(defaultCsp==null){
            log.warn("Before using CSP, please call initCspFactories(). Falling back to bootCsp.");
            //IFactoryOpts opts = new GmFactoryOpts(256, "SM3", true, true, "",true);
            IFactoryOpts opts = new GmFactoryOpts();
            GmCspFactory factory=new GmCspFactory();
            bootCsp=factory.getCsp(opts);
            return bootCsp;
        }
        return defaultCsp;
    }

    //根据工厂名称返回指定的CSP
    public static ICsp getCsp(String name){
        ICsp csp = cspMap.get(name);
        if(csp==null){
            log.error("Get Csp {} failed",name);
            return null;
        }
        return csp;
    }



}
