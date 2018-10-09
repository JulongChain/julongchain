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
package org.bcia.julongchain.csp.gm.dxct;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.csp.intfs.IKey;

/**
 * 密钥存储接口
 *
 * @author zhangmingyang
 * @Date: 2018/4/28
 * @company Dingxuan
 */
public interface IKeyStore {
    /**
     * 只读返回 true,返回值为true时,存储密钥将失败
     *
     * @return
     */
    boolean readOnly();

    /**
     * 获取蜜钥，返回一个密钥对象,其ski是传递的密钥对象
     *
     * @param ski
     * @return
     * @throws JulongChainException
     */
    IKey getKey(byte[] ski) throws JulongChainException;

    /**
     * 存储密钥到密钥库,如果密钥库是制度的
     * 该方法无效
     *
     * @param ikey
     */
    void storeKey(IKey ikey);
}
