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
package org.bcia.javachain.common.configtx.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.configtx.ConfigComparable;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.protos.common.Configtx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/24
 * @company Dingxuan
 */
public class ConfigMapUtils {
    private static final String CHILD_PREFIX = "[Tree]   ";
    private static final String VALUE_PREFIX = "[Value]  ";
    private static final String POLICY_PREFIX = "[Policy] ";

    private static final int MAX_LENGTH = 249;
    private static final String REGEX_GROUP_ID = "[a-z][a-z0-9.-]*";
    private static final String REGEX_CONFIG_ID = "[a-zA-Z0-9.-]+";
    private static final String[] ILLEGAL_NAMES = {".", ".."};

    public static Map<String, ConfigComparable> mapConfig(Configtx.ConfigTree configTree, String rootKey) throws
            ValidateException {
        Map<String, ConfigComparable> result = new HashMap<String, ConfigComparable>();

        if (configTree != null) {
            recurseMapConfig(result, new String[]{rootKey}, configTree);
        }

        return result;
    }

    private static void recurseMapConfig(Map<String, ConfigComparable> comparableMap, String[] paths, Configtx.ConfigTree
            configTree) throws ValidateException {
        String[] newPaths = new String[paths.length - 1];
        System.arraycopy(paths, 0, newPaths, 0, paths.length - 1);

        ConfigComparable<Configtx.ConfigTree> configTreeComparable = new ConfigComparable<Configtx.ConfigTree>(paths[paths.length -
                1], newPaths, configTree);
        addToMap(comparableMap, configTreeComparable);

        //遍历所有的子树
        Iterator<Map.Entry<String, Configtx.ConfigTree>> childIterator = configTree.getChildsMap().entrySet()
                .iterator();
        while (childIterator.hasNext()) {
            Map.Entry<String, Configtx.ConfigTree> entry = childIterator.next();
            String childName = entry.getKey();
            Configtx.ConfigTree childTree = entry.getValue();

            String[] childPaths = new String[paths.length + 1];
            System.arraycopy(paths, 0, childPaths, 0, paths.length);
            childPaths[paths.length] = childName;

            recurseMapConfig(comparableMap, childPaths, childTree);
        }

        //遍历所有的值
        Iterator<Map.Entry<String, Configtx.ConfigValue>> valueIterator = configTree.getValuesMap().entrySet()
                .iterator();
        while (valueIterator.hasNext()) {
            Map.Entry<String, Configtx.ConfigValue> entry = valueIterator.next();
            String valueName = entry.getKey();
            Configtx.ConfigValue configValue = entry.getValue();

            ConfigComparable<Configtx.ConfigValue> configValueComparable = new ConfigComparable<Configtx.ConfigValue>(valueName,
                    paths, configValue);
            addToMap(comparableMap, configValueComparable);
        }

        //遍历所有的策略
        Iterator<Map.Entry<String, Configtx.ConfigPolicy>> policyIterator = configTree.getPoliciesMap().entrySet()
                .iterator();
        while (policyIterator.hasNext()) {
            Map.Entry<String, Configtx.ConfigPolicy> entry = policyIterator.next();
            String policyName = entry.getKey();
            Configtx.ConfigPolicy configPolicy = entry.getValue();

            ConfigComparable<Configtx.ConfigPolicy> configPolicyComparable = new ConfigComparable<Configtx.ConfigPolicy>(policyName,
                    paths, configPolicy);
            addToMap(comparableMap, configPolicyComparable);
        }
    }

    private static void addToMap(Map<String, ConfigComparable> comparableMap, ConfigComparable comparable) throws ValidateException {
        String prefix = null;

        Object obj = comparable.getT();
        if (obj instanceof Configtx.ConfigTree) {
            prefix = CHILD_PREFIX;
        } else if (obj instanceof Configtx.ConfigValue) {
            prefix = VALUE_PREFIX;
        } else if (obj instanceof Configtx.ConfigPolicy) {
            prefix = POLICY_PREFIX;
        }

        validateConfigId(comparable.getKey());

        if (ArrayUtils.isEmpty(comparable.getPath())) {
            prefix += CommConstant.PATH_SEPARATOR + comparable.getKey();
        } else {
            prefix += CommConstant.PATH_SEPARATOR + StringUtils.join(comparable.getPath(), CommConstant.PATH_SEPARATOR)
                    + CommConstant.PATH_SEPARATOR + comparable.getKey();
        }

        comparableMap.put(prefix, comparable);
    }

    private static void validateConfigId(String configId) throws ValidateException {
        if (StringUtils.isBlank(configId)) {
            throw new ValidateException("configId can not be null");
        }

        if (configId.length() > MAX_LENGTH) {
            throw new ValidateException("configId cannot be longer than max length");
        }

        if (ArrayUtils.contains(ILLEGAL_NAMES, configId)) {
            throw new ValidateException("illegal configId");
        }

        if (!Pattern.matches(REGEX_CONFIG_ID, configId)) {
            throw new ValidateException("Wrong configId");
        }
    }

    private static void validateGroupId(String groupId) throws ValidateException {
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

}
