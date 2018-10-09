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
package org.bcia.julongchain.csp.pkcs11;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import sun.security.pkcs11.wrapper.PKCS11;

/**
 * 基于PKCS11实现的Factory接口定义
 *
 * @author Ying Xu
 * @date 4/19/18
 * @company FEITIAN
 */
public interface IPKCS11FactoryOpts extends IFactoryOpts {

    // get pkcs11
    public PKCS11 getPKCS11();

    // 获取 session 句柄
    public long getSessionhandle();

    // get soft verify flag
    public boolean getSoftVerify();

    // 是否导出，对于硬实现私钥无效，因为私钥无法导出
    public boolean getNoImport();

    // 
    public void optFinalized() throws CspException;

    // 获取PIN码
    public char[] getPin();
}
