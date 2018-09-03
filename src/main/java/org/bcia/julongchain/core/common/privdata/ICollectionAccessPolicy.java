/*
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
package org.bcia.julongchain.core.common.privdata;

import org.bcia.julongchain.common.util.proto.SignedData;

import java.util.List;

/**
 * 集合访问策略
 *
 * @author sunianle, sunzongyu
 * @date 4/27/18
 * @company Dingxuan
 */
public interface ICollectionAccessPolicy {
    /**
	 * 判断是否通过
     */
    boolean getAccessFilter(SignedData sd);

    /**
	 * 获取请求的Node数量
     */
    int getRequiredNodeCount();

    /**
	 * 获取最大Node数量
     */
    int getMaximumNodeCount();

    /**
	 * 获取所有成员
     */
    List<String> memberOrgs();
}