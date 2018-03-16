/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.javachain.msp.mgmt;

import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.msp.IMsp;
import org.bcia.javachain.msp.IMspManager;
import org.bcia.javachain.msp.entity.Identity;

/**
 * @author zhangmingyang
 * @Date: 2018/3/14
 * @company Dingxuan
 */
public class Mgmt  {
    /**
     * 加载本地msp
     * @param localmspdir  本地msp目录
     * @param bccspconfig  bccsp配置
     * @param mspID         mspid
     */
    public void loadlocalMsp(String localmspdir,Object bccspconfig,String mspID){

    }

    /**
     * 获取本地msp
     * @return
     */
    public  IMsp getLocalMsp(){

        return null;
    }

    /**
     * 身份序列化
     * @param chainID
     * @return
     */
    public IIdentityDeserializer getIdentityDeserializer(String  chainID){
        return getManagerForChain(chainID);
    }

    /**
     * 从链上获取一个管理者,如果没有这样的管理者,则创建一个
     * @param chainID
     * @return
     */
    public IMspManager getManagerForChain(String chainID){
        return null;
    }

}
