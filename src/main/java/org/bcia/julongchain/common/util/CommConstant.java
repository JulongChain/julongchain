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
package org.bcia.julongchain.common.util;

/**
 * 公共常量
 *
 * @author zhouhui
 * @date 2018/3/19
 * @company Dingxuan
 */
public class CommConstant {
    /**
     * 默认编码
     */
    public static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * 原数据：版本
     */
    public static final String METADATA_VERSION = "development build";
    /**
     * 背书系统智能合约
     */
    public static final String ESSC = "essc";
    /**
     * 生命周期系统智能合约
     */
    public static final String LSSC = "lssc";

    /**
     * 配置系统智能合约
     */
    public static final String CSSC = "cssc";

    /**
     * 查询系统智能合约
     */
    public static final String QSSC = "qssc";
    /**
     * 验证系统智能合约
     */
    public static final String VSSC = "vssc";

    /**
     * 成员服务提供者类型
     */
    public static final int MSPTYPE_CSP_VALUE = 0;

    /**
     * 部署
     */
    public static final String DEPLOY = "deploy";

    /**
     * 升级
     */
    public static final String UPGRADE = "upgrade";

    /**
     * 路径分隔符
     */
    public static final String PATH_SEPARATOR = "/";

    public static final String CONFIG_DIR_PREFIX = "config/";

    /**
     * 默认随机数长度
     */
    public static final int DEFAULT_NONCE_LENGTH = 24;

    /**
     * gRPC最大消息大小(512M)
     */
    public static final int MAX_GRPC_MESSAGE_SIZE = 1 << 29;

}
