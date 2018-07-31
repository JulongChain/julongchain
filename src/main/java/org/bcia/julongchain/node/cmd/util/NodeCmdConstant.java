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
package org.bcia.julongchain.node.cmd.util;

/**
 * 命令行支持常量
 *
 * @author zhouhui
 * @date 2018/3/1
 * @company Dingxuan
 */
public class NodeCmdConstant {
    /**
     * 创建群组
     */
    public static final String CREATE_GROUP = "create";
    /**
     * 加入群组
     */
    public static final String JOIN_GROUP = "join";
    /**
     * 列出所加入的群组
     */
    public static final String LIST_GROUP = "list";
    /**
     * 更新群组配置
     */
    public static final String UPDATE_GROUP = "update";
    /**
     * 启动Node服务
     */
    public static final String START_SERVER = "start";
    /**
     * Node服务状态
     */
    public static final String SERVER_STATUS = "status";
    /**
     * 安装智能合约
     */
    public static final String INSTALL_CONTRACT = "install";
    /**
     * 实例化智能合约
     */
    public static final String INSTANCE_CONTRACT = "instantiate";
    /**
     * 调用智能合约
     */
    public static final String INVOKE_CONTRACT = "invoke";
    /**
     * 调用智能合约
     */
    public static final String QUERY_CONTRACT = "query";
}
