/**
 * Copyright Dingxuan. All Rights Reserved.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.bcia.julongchain.common.ssh;

import com.jcraft.jsch.JSchException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

/**
 * ssh登录远程服务器，执行命令的帮助类
 *
 * @author wanliangbing
 * @date 2018-02-28
 * @company Dingxuan
 */
public class SshDocker {

    private static JavaChainLog log = JavaChainLogFactory.getLog(SshDocker.class);

    /**
     * 登录主机并执行命令
     *
     * @param host 主机
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @param command 命令
     */
    public static void executeCommand(String host,Integer port,String username,String password,String command){
        try {
            SshHelper helper = new SshHelper(host, port, username, password);
            try {
                SshResInfo resInfo =helper.sendCmd(command);
                log.info(resInfo.toString());
                helper.close();
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        } catch (JSchException e) {
            log.error(e.getMessage(),e);
        }
    }

}
