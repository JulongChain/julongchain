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
package org.bcia.julongchain.common.configtx.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.configtx.ConfigComparable;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.common.Configtx;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 配置映射工具类
 *
 * @author zhouhui
 * @date 2018/4/24
 * @company Dingxuan
 */
public class ConfigMapUtils {
    private static final String CHILD_PREFIX = "[Tree]   ";
    private static final String VALUE_PREFIX = "[Value]  ";
    private static final String POLICY_PREFIX = "[Policy] ";

    /**
     * 配置短路径最大长度
     */
    private static final int MAX_PATH_LENGTH = 249;
    /**
     * 合法短路径对应的正则表达式
     */
    private static final String REGEX_CONFIG_ID = "[a-zA-Z0-9.-]+";
    /**
     * 无效的短路径
     */
    private static final String[] ILLEGAL_NAMES = {".", ".."};

    /**
     * 将配置树对象映射成可比较的对象集合
     *
     * @param configTree
     * @param rootKey
     * @return
     * @throws ValidateException
     */
    public static Map<String, ConfigComparable> toComparableMap(Configtx.ConfigTree configTree, String rootKey) throws
            ValidateException {
        Map<String, ConfigComparable> result = new ConcurrentHashMap<>();

        if (configTree != null) {
            recurseMapConfigTree(result, new String[]{rootKey}, configTree);
        }

        return result;
    }

    /**
     * 递归映射配置树
     *
     * @param comparableMap 目标集合
     * @param paths         当前路径（含父路径+当前路径）
     * @param configTree    要被映射的配置树对象
     * @throws ValidateException
     */
    private static void recurseMapConfigTree(Map<String, ConfigComparable> comparableMap, String[] paths, Configtx
            .ConfigTree configTree) throws ValidateException {
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

            recurseMapConfigTree(comparableMap, childPaths, childTree);
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

    /**
     * 向目标映射集合加入比较对象
     *
     * @param comparableMap
     * @param comparable
     * @throws ValidateException
     */
    private static void addToMap(Map<String, ConfigComparable> comparableMap, ConfigComparable comparable) throws
            ValidateException {
        //校验当前比对对象的路径标识是否合法
        validateConfigPath(comparable.getKey());

        String prefix = null;
        Object obj = comparable.getT();
        if (obj instanceof Configtx.ConfigTree) {
            prefix = CHILD_PREFIX;
        } else if (obj instanceof Configtx.ConfigValue) {
            prefix = VALUE_PREFIX;
        } else if (obj instanceof Configtx.ConfigPolicy) {
            prefix = POLICY_PREFIX;
        }

        //拼接父路径和当前短路径，形成长路径
        if (ArrayUtils.isEmpty(comparable.getPath())) {
            prefix += CommConstant.PATH_SEPARATOR + comparable.getKey();
        } else {
            prefix += CommConstant.PATH_SEPARATOR + StringUtils.join(comparable.getPath(), CommConstant.PATH_SEPARATOR)
                    + CommConstant.PATH_SEPARATOR + comparable.getKey();
        }

        comparableMap.put(prefix, comparable);
    }

    /**
     * 校验配置路径（短路径）
     *
     * @param path
     * @throws ValidateException
     */
    public static void validateConfigPath(String path) throws ValidateException {
        if (StringUtils.isBlank(path)) {
            throw new ValidateException("Config path can not be null");
        }

        if (path.length() > MAX_PATH_LENGTH) {
            throw new ValidateException("Config path can not be longer than max length");
        }

        if (ArrayUtils.contains(ILLEGAL_NAMES, path)) {
            throw new ValidateException("Illegal path: " + path);
        }

        if (!Pattern.matches(REGEX_CONFIG_ID, path)) {
            throw new ValidateException("Wrong config path: " + path);
        }
    }

    /**
     * 将可比较的配置集合还原成配置树
     *
     * @param configComparableMap
     * @param namespace
     * @return
     * @throws ValidateException
     */
    public static Configtx.ConfigTree restoreConfigTree(Map<String, ConfigComparable> configComparableMap,
                                                        String namespace) throws ValidateException {
        String rootPath = CommConstant.PATH_SEPARATOR + namespace;
        return recurseRestore(rootPath, configComparableMap);
    }

    /**
     * 递归还原配置树
     *
     * @param path                长路径
     * @param configComparableMap
     * @return
     * @throws ValidateException
     */
    private static Configtx.ConfigTree recurseRestore(String path, Map<String, ConfigComparable> configComparableMap)
            throws ValidateException {
        String childPath = CHILD_PREFIX + path;
        ConfigComparable childConfigComparable = configComparableMap.get(childPath);

        ValidateUtils.isNotNull(childConfigComparable, "ChildConfigComparable can not be null");
        ValidateUtils.isNotNull(childConfigComparable.getT(), "ChildConfigComparable.getT can not be null");
        if (!(childConfigComparable.getT() instanceof Configtx.ConfigTree)) {
            throw new ValidateException("Should be ConfigTree instance: " + childConfigComparable.getT());
        }

        Configtx.ConfigTree configTree = (Configtx.ConfigTree) childConfigComparable.getT();
        Configtx.ConfigTree.Builder newConfigTreeBuilder = configTree.toBuilder();

        //遍历所有的子树
        Iterator<Map.Entry<String, Configtx.ConfigTree>> childIterator = configTree.getChildsMap().entrySet()
                .iterator();
        while (childIterator.hasNext()) {
            Map.Entry<String, Configtx.ConfigTree> entry = childIterator.next();
            String childName = entry.getKey();

            Configtx.ConfigTree newChildTree = recurseRestore(path + CommConstant.PATH_SEPARATOR + childName,
                    configComparableMap);
            newConfigTreeBuilder.putChilds(childName, newChildTree);
        }

        //遍历所有的值
        Iterator<Map.Entry<String, Configtx.ConfigValue>> valueIterator = configTree.getValuesMap().entrySet()
                .iterator();
        while (valueIterator.hasNext()) {
            Map.Entry<String, Configtx.ConfigValue> entry = valueIterator.next();
            String valueName = entry.getKey();

            String valuePath = VALUE_PREFIX + path + CommConstant.PATH_SEPARATOR + valueName;

            ConfigComparable configComparable = configComparableMap.get(valuePath);

            ValidateUtils.isNotNull(configComparable, "ConfigComparable can not be null");
            ValidateUtils.isNotNull(configComparable.getT(), "ConfigComparable.getT can not be null");
            if (!(configComparable.getT() instanceof Configtx.ConfigValue)) {
                throw new ValidateException("Should be ConfigValue instance: " + configComparable.getT());
            }

            newConfigTreeBuilder.putValues(valueName, (Configtx.ConfigValue) configComparable.getT());
        }

        //遍历所有的策略
        Iterator<Map.Entry<String, Configtx.ConfigPolicy>> policyIterator = configTree.getPoliciesMap().entrySet()
                .iterator();
        while (policyIterator.hasNext()) {
            Map.Entry<String, Configtx.ConfigPolicy> entry = policyIterator.next();
            String policyName = entry.getKey();

            String policyPath = POLICY_PREFIX + path + CommConstant.PATH_SEPARATOR + policyName;

            ConfigComparable configComparable = configComparableMap.get(policyPath);

            ValidateUtils.isNotNull(configComparable, "ConfigComparable can not be null");
            ValidateUtils.isNotNull(configComparable.getT(), "ConfigComparable.getT can not be null");
            if (!(configComparable.getT() instanceof Configtx.ConfigPolicy)) {
                throw new ValidateException("Should be ConfigPolicy instance: " + configComparable.getT());
            }

            newConfigTreeBuilder.putPolicies(policyName, (Configtx.ConfigPolicy) configComparable.getT());
        }

        return newConfigTreeBuilder.build();
    }
}