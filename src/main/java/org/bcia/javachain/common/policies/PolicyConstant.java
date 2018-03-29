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
package org.bcia.javachain.common.policies;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/3/9
 * @company Dingxuan
 */
public class PolicyConstant {
    public static final String PATH_SEPARATOR = "/";

    public static final String GROUP_PREFIX = "Group";

    public static final String APPLICATION_PREFIX = "App";

    public static final String CONSENTER_PREFIX = "Consenter";

    public static final String GROUP_READERS = PATH_SEPARATOR + GROUP_PREFIX + PATH_SEPARATOR + "Readers";

    public static final String GROUP_WRITERS = PATH_SEPARATOR + GROUP_PREFIX + PATH_SEPARATOR + "Writers";

    public static final String GROUP_APP_READERS = PATH_SEPARATOR + GROUP_PREFIX + PATH_SEPARATOR +
            APPLICATION_PREFIX + PATH_SEPARATOR + "Readers";

    public static final String GROUP_APP_WRITERS = PATH_SEPARATOR + GROUP_PREFIX + PATH_SEPARATOR +
            APPLICATION_PREFIX + PATH_SEPARATOR + "Writers";

    public static final String GROUP_APP_ADMINS = PATH_SEPARATOR + GROUP_PREFIX + PATH_SEPARATOR +
            APPLICATION_PREFIX + PATH_SEPARATOR + "Admins";

    public static final String BLOCK_VALIDATION = PATH_SEPARATOR + GROUP_PREFIX + PATH_SEPARATOR + CONSENTER_PREFIX +
            PATH_SEPARATOR + "BlockValidation";
}
