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
package org.bcia.javachain.common.groupconfig.capability;

/**
 * 共识支持能力对象
 *
 * @author zhouhui
 * @date 2018/5/9
 * @company Dingxuan
 * @deprecated 使用ConsenterProvider代替
 */
public class ConsenterCapabilities implements IConsenterCapabilities {
    private boolean supported;
    private boolean predictableGroupTemplate;
    private boolean resubmission;
    private boolean expiration;

    @Override
    public boolean isSupported() {
        return supported;
    }

    @Override
    public boolean isPredictableGroupTemplate() {
        return predictableGroupTemplate;
    }

    @Override
    public boolean isResubmission() {
        return resubmission;
    }

    @Override
    public boolean isExpiration() {
        return expiration;
    }
}
