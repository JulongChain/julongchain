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
package org.bcia.julongchain.node.common.helper;

import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.common.Configtx;

import java.util.*;

/**
 * 配置更新对象帮助类
 *
 * @author zhouhui
 * @date 2018/3/28
 * @company Dingxuan
 */
public class ConfigUpdateHelper {
    public static Configtx.ConfigUpdate compute(Configtx.Config originalConfig, Configtx.Config pendingConfig) throws
            ValidateException {
        ValidateUtils.isNotNull(originalConfig, "originalConfig can not be null");
        ValidateUtils.isNotNull(pendingConfig, "pendingConfig can not be null");
        ValidateUtils.isNotNull(originalConfig.getGroupTree(), "No group tree found in original");
        ValidateUtils.isNotNull(pendingConfig.getGroupTree(), "No group tree found in pending");

        Object[] childObjs = computeTreeUpdate(originalConfig.getGroupTree(), pendingConfig.getGroupTree());
        Configtx.ConfigTree readChild = (Configtx.ConfigTree) childObjs[0];
        Configtx.ConfigTree writeChild = (Configtx.ConfigTree) childObjs[1];
        boolean childUpdate = (boolean) childObjs[2];

        if (!childUpdate) {
            throw new ValidateException("No differences between original and pending");
        }

        //开始构造ConfigUpdate对象
        Configtx.ConfigUpdate.Builder configUpdateBuilder = Configtx.ConfigUpdate.newBuilder();
        configUpdateBuilder.setReadSet(readChild);
        configUpdateBuilder.setWriteSet(writeChild);

        return configUpdateBuilder.build();
    }

    /**
     * 计算策略集合更新
     *
     * @param originalMap
     * @param pendingMap
     * @return
     */
    private static Object[] computePoliciesMapUpdate(Map<String, Configtx.ConfigPolicy> originalMap, Map<String,
            Configtx.ConfigPolicy> pendingMap) {
        //TODO:如果有空的情况
        Map<String, Configtx.ConfigPolicy> readSet = new HashMap<String, Configtx.ConfigPolicy>();
        Map<String, Configtx.ConfigPolicy> writeSet = new HashMap<String, Configtx.ConfigPolicy>();
        Map<String, Configtx.ConfigPolicy> sameSet = new HashMap<String, Configtx.ConfigPolicy>();

        boolean hasMembersUpdate = false;

        //先循环originalMap
        Iterator<Map.Entry<String, Configtx.ConfigPolicy>> entries = originalMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Configtx.ConfigPolicy> entry = entries.next();
            String policyName = entry.getKey();
            Configtx.ConfigPolicy originalPolicy = entry.getValue();

            //判断在新集合pendingMap有没有对应项
            Configtx.ConfigPolicy pendingPolicy = pendingMap.get(policyName);
            if (pendingPolicy == null) {
                //该成员在新集合不存在，很可能被删除，说明集合成员项有更新
                hasMembersUpdate = true;
                continue;
            }

            //如果存在对应项，相同的并入sameSet，不同的并入writeSet
            if (pendingPolicy.getModPolicy().equals(originalPolicy.getModPolicy()) &&
                    Arrays.equals(originalPolicy.getPolicy().toByteArray(), pendingPolicy.getPolicy().toByteArray())) {
                sameSet.put(policyName, originalPolicy);
            } else {
                //基于新集合创建另一个集合，只是版本增加1
                Configtx.ConfigPolicy.Builder newVersionPolicyBuilder = Configtx.ConfigPolicy.newBuilder(pendingPolicy);
                newVersionPolicyBuilder.setVersion(pendingPolicy.getVersion() + 1);
                Configtx.ConfigPolicy newVersionPolicy = newVersionPolicyBuilder.build();

                writeSet.put(policyName, newVersionPolicy);
            }
        }

        //再循环pendingMap
        Iterator<Map.Entry<String, Configtx.ConfigPolicy>> pendingEntries = pendingMap.entrySet().iterator();
        while (pendingEntries.hasNext()) {
            Map.Entry<String, Configtx.ConfigPolicy> entry = pendingEntries.next();
            String policyName = entry.getKey();
            Configtx.ConfigPolicy pendingPolicy = entry.getValue();

            //判断在原集合originalMap有没有对应项。有对应项的已经在前面处理过了，只需要再处理不存在的
            Configtx.ConfigPolicy originalPolicy = originalMap.get(policyName);
            if (originalPolicy == null) {
                //该成员在原集合不存在，很可能是新增项，说明集合成员项有更新
                hasMembersUpdate = true;

                //基于新集合创建另一个集合，只是版本为0.表示新增项
                Configtx.ConfigPolicy.Builder newVersionPolicyBuilder = Configtx.ConfigPolicy.newBuilder(pendingPolicy);
                newVersionPolicyBuilder.setVersion(0);
                Configtx.ConfigPolicy newVersionPolicy = newVersionPolicyBuilder.build();

                writeSet.put(policyName, newVersionPolicy);
            }
        }

        return new Object[]{readSet, writeSet, sameSet, hasMembersUpdate};
    }

    private static Object[] computeValuesMapUpdate(Map<String, Configtx.ConfigValue> originalMap, Map<String,
            Configtx.ConfigValue> pendingMap) {
        //TODO:如果有空的情况
        Map<String, Configtx.ConfigValue> readSet = new HashMap<String, Configtx.ConfigValue>();
        Map<String, Configtx.ConfigValue> writeSet = new HashMap<String, Configtx.ConfigValue>();
        Map<String, Configtx.ConfigValue> sameSet = new HashMap<String, Configtx.ConfigValue>();

        boolean hasMembersUpdate = false;

        //先循环originalMap
        Iterator<Map.Entry<String, Configtx.ConfigValue>> entries = originalMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Configtx.ConfigValue> entry = entries.next();
            String valueName = entry.getKey();
            Configtx.ConfigValue originalValue = entry.getValue();

            //判断在新集合pendingMap有没有对应项
            Configtx.ConfigValue pendingValue = pendingMap.get(valueName);
            if (pendingValue == null) {
                //该成员在新集合不存在，很可能被删除，说明集合成员项有更新
                hasMembersUpdate = true;
                continue;
            }

            //如果存在对应项，相同的并入sameSet，不同的并入writeSet
            if (pendingValue.getModPolicy().equals(originalValue.getModPolicy())
                    && Arrays.equals(originalValue.getValue().toByteArray(), pendingValue.getValue().toByteArray())) {
                sameSet.put(valueName, originalValue);
            } else {
                //基于新集合创建另一个集合，只是版本增加1
                Configtx.ConfigValue.Builder newVersionValueBuilder = Configtx.ConfigValue.newBuilder(pendingValue);
                newVersionValueBuilder.setVersion(pendingValue.getVersion() + 1);
                Configtx.ConfigValue newVersionValue = newVersionValueBuilder.build();

                writeSet.put(valueName, newVersionValue);
            }
        }

        //再循环pendingMap
        Iterator<Map.Entry<String, Configtx.ConfigValue>> pendingEntries = pendingMap.entrySet().iterator();
        while (pendingEntries.hasNext()) {
            Map.Entry<String, Configtx.ConfigValue> entry = pendingEntries.next();
            String valueName = entry.getKey();
            Configtx.ConfigValue pendingValue = entry.getValue();

            //判断在原集合originalMap有没有对应项。有对应项的已经在前面处理过了，只需要再处理不存在的
            Configtx.ConfigValue originalValue = originalMap.get(valueName);
            if (originalValue == null) {
                //该成员在原集合不存在，很可能是新增项，说明集合成员项有更新
                hasMembersUpdate = true;

                //基于新集合创建另一个集合，只是版本为0.表示新增项
                Configtx.ConfigValue.Builder newVersionValueBuilder = Configtx.ConfigValue.newBuilder(pendingValue);
                newVersionValueBuilder.setVersion(0);
                Configtx.ConfigValue newVersionValue = newVersionValueBuilder.build();

                writeSet.put(valueName, newVersionValue);
            }
        }

        return new Object[]{readSet, writeSet, sameSet, hasMembersUpdate};
    }

    private static Object[] computeChildsMapUpdate(Map<String, Configtx.ConfigTree> originalMap, Map<String,
            Configtx.ConfigTree> pendingMap) {
        //TODO:如果有空的情况
        Map<String, Configtx.ConfigTree> readSet = new HashMap<String, Configtx.ConfigTree>();
        Map<String, Configtx.ConfigTree> writeSet = new HashMap<String, Configtx.ConfigTree>();
        Map<String, Configtx.ConfigTree> sameSet = new HashMap<String, Configtx.ConfigTree>();

        boolean hasMemberUpdate = false;

        //先循环originalMap
        Iterator<Map.Entry<String, Configtx.ConfigTree>> entries = originalMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Configtx.ConfigTree> entry = entries.next();
            String childName = entry.getKey();
            Configtx.ConfigTree originalTree = entry.getValue();

            //判断在新集合pendingMap有没有对应项
            Configtx.ConfigTree pendingTree = pendingMap.get(childName);
            if (pendingTree == null) {
                //该成员在新集合不存在，很可能被删除，说明集合成员项有更新
                hasMemberUpdate = true;
                continue;
            }

            //如果存在对应项，相同的并入sameSet，不同的并入writeSet
            Object[] childObjs = computeTreeUpdate(originalTree, pendingTree);
            Configtx.ConfigTree readTree = (Configtx.ConfigTree) childObjs[0];
            Configtx.ConfigTree writeTree = (Configtx.ConfigTree) childObjs[1];
            boolean childUpdate = (boolean) childObjs[2];

            if (!childUpdate) {
                sameSet.put(childName, readTree);
            } else {
                readSet.put(childName, readTree);
                writeSet.put(childName, writeTree);
            }
        }

        //再循环pendingMap
        Iterator<Map.Entry<String, Configtx.ConfigTree>> pendingEntries = pendingMap.entrySet().iterator();
        while (pendingEntries.hasNext()) {
            Map.Entry<String, Configtx.ConfigTree> entry = pendingEntries.next();
            String childName = entry.getKey();
            Configtx.ConfigTree pendingTree = entry.getValue();

            //判断在原集合originalMap有没有对应项。有对应项的已经在前面处理过了，只需要再处理不存在的
            Configtx.ConfigTree originalTree = originalMap.get(childName);
            if (originalTree == null) {
                //该成员在原集合不存在，很可能是新增项，说明集合成员项有更新
                hasMemberUpdate = true;

                //基于新集合创建另一个集合，只是版本为0.表示新增项
                Configtx.ConfigTree.Builder newVersionTreeBuilder = Configtx.ConfigTree.newBuilder(pendingTree);
                newVersionTreeBuilder.setVersion(0);
                Configtx.ConfigTree newVersionTree = newVersionTreeBuilder.build();

                writeSet.put(childName, newVersionTree);
            }
        }

        return new Object[]{readSet, writeSet, sameSet, hasMemberUpdate};
    }


    private static Object[] computeTreeUpdate(Configtx.ConfigTree originalTree, Configtx.ConfigTree pendingTree) {
        Object[] policiesObjs = computePoliciesMapUpdate(originalTree.getPoliciesMap(), pendingTree.getPoliciesMap());
        Object[] valuesObjs = computeValuesMapUpdate(originalTree.getValuesMap(), pendingTree.getValuesMap());
        Object[] childsObjs = computeChildsMapUpdate(originalTree.getChildsMap(), pendingTree.getChildsMap());

        Map<String, Configtx.ConfigPolicy> policiesReadSet = (Map<String, Configtx.ConfigPolicy>) policiesObjs[0];
        Map<String, Configtx.ConfigPolicy> policiesWriteSet = (Map<String, Configtx.ConfigPolicy>) policiesObjs[1];
        Map<String, Configtx.ConfigPolicy> policiesSameSet = (Map<String, Configtx.ConfigPolicy>) policiesObjs[2];
        boolean policiesMembersUpdate = (boolean) policiesObjs[3];

        Map<String, Configtx.ConfigValue> valuesReadSet = (Map<String, Configtx.ConfigValue>) valuesObjs[0];
        Map<String, Configtx.ConfigValue> valuesWriteSet = (Map<String, Configtx.ConfigValue>) valuesObjs[1];
        Map<String, Configtx.ConfigValue> valuesSameSet = (Map<String, Configtx.ConfigValue>) valuesObjs[2];
        boolean valuesMembersUpdate = (boolean) valuesObjs[3];

        Map<String, Configtx.ConfigTree> childsReadSet = (Map<String, Configtx.ConfigTree>) childsObjs[0];
        Map<String, Configtx.ConfigTree> childsWriteSet = (Map<String, Configtx.ConfigTree>) childsObjs[1];
        Map<String, Configtx.ConfigTree> childsSameSet = (Map<String, Configtx.ConfigTree>) childsObjs[2];
        boolean childsMembersUpdate = (boolean) childsObjs[3];

        if (!policiesMembersUpdate && !valuesMembersUpdate && !childsMembersUpdate && Objects.equals(originalTree
                .getModPolicy(), pendingTree.getModPolicy())) {
            //所有对象都未更新成员，且权限亦未更新
            if (policiesReadSet.size() <= 0 && policiesWriteSet.size() <= 0
                    && valuesReadSet.size() <= 0 && valuesWriteSet.size() <= 0
                    && childsReadSet.size() <= 0 && childsWriteSet.size() <= 0) {
                //完全未更改
                Configtx.ConfigTree resultSet = Configtx.ConfigTree.newBuilder().setVersion(originalTree.getVersion())
                        .build();
                Configtx.ConfigTree writeSet = Configtx.ConfigTree.newBuilder().setVersion(originalTree.getVersion())
                        .build();

                return new Object[]{resultSet, writeSet, false};
            } else {
                //部分更改
                Configtx.ConfigTree resultSet = Configtx.ConfigTree.newBuilder().setVersion(originalTree.getVersion())
                        .putAllPolicies(policiesReadSet).putAllValues(valuesReadSet).putAllChilds(childsReadSet).build();
                Configtx.ConfigTree writeSet = Configtx.ConfigTree.newBuilder().setVersion(originalTree.getVersion())
                        .putAllPolicies(policiesWriteSet).putAllValues(valuesWriteSet).putAllChilds(childsWriteSet).build();

                return new Object[]{resultSet, writeSet, true};
            }
        }

        policiesReadSet.putAll(policiesSameSet);
        policiesWriteSet.putAll(policiesSameSet);

        valuesReadSet.putAll(valuesSameSet);
        valuesWriteSet.putAll(valuesSameSet);

        childsReadSet.putAll(childsSameSet);
        childsWriteSet.putAll(childsSameSet);

        //读集合为原版本，写集合新增1
        Configtx.ConfigTree resultSet = Configtx.ConfigTree.newBuilder().setVersion(originalTree.getVersion())
                .putAllPolicies(policiesReadSet).putAllValues(valuesReadSet).putAllChilds(childsReadSet).build();
        Configtx.ConfigTree writeSet = Configtx.ConfigTree.newBuilder().setVersion(originalTree.getVersion() + 1)
                .putAllPolicies(policiesWriteSet).putAllValues(valuesWriteSet).putAllChilds(childsWriteSet)
                .setModPolicy(pendingTree.getModPolicy()).build();

        return new Object[]{resultSet, writeSet, true};
    }
}
