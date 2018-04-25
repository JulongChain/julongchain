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
package org.bcia.javachain.common.configtx;

import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.policies.IPolicyManager;
import org.bcia.javachain.common.util.ValidateUtils;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;

import java.util.regex.Pattern;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/24
 * @company Dingxuan
 */
public class Validator implements IValidator {
    private static final int MAX_LENGTH = 249;
    private static final String REGEX_GROUP_ID = "[a-z][a-z0-9.-]*";
    private static final String REGEX_CONFIG_ID = "[a-zA-Z0-9.-]+";


    private String groupId;
    private long sequence;
    private Configtx.Config config;
    private String namespace;
    private IPolicyManager policyManager;

    public Validator(String groupId, Configtx.Config config, String namespace, IPolicyManager policyManager) throws
            ValidateException {
        this.groupId = groupId;
        this.config = config;
        this.namespace = namespace;
        this.policyManager = policyManager;

        ValidateUtils.isNotNull(config, "config can not be null");
        ValidateUtils.isNotNull(config.getGroupTree(), "config.tree can not be null");

        validateGroupId(groupId);


    }

    private void validateGroupId(String groupId) throws ValidateException {
        if (StringUtils.isBlank(groupId)) {
            throw new ValidateException("groupId can not be null");
        }

        if (groupId.length() > MAX_LENGTH) {
            throw new ValidateException("groupId cannot be longer than max length");
        }

        if (!Pattern.matches(REGEX_GROUP_ID, groupId)) {
            throw new ValidateException("Wrong groupId");
        }
    }

    @Override
    public void validate(Configtx.ConfigEnvelope configEnv) {

    }

    @Override
    public Configtx.ConfigEnvelope proposeConfigUpdate(Common.Envelope configtx) {
        return null;
    }

    @Override
    public String groupId() {
        return null;
    }

    @Override
    public Configtx.Config configProto() {
        return null;
    }

    @Override
    public long sequence() {
        return 0;
    }
}
