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
package org.bcia.javachain.node.common.helper;

import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.protos.common.Configtx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/28
 * @company Dingxuan
 */
public class ConfigUpdateHelper {
    public static Configtx.ConfigUpdate compute(Configtx.Config original, Configtx.Config pending) throws
            ValidateException {
        if (original.getGroupChild() == null) {
            throw new ValidateException("No group child found in original");
        }

        if (pending.getGroupChild() == null) {
            throw new ValidateException("No group child found in pending");
        }

        Object[] childObjs = computeChildUpdate(original.getGroupChild(), pending.getGroupChild());
        Configtx.ConfigChild readChild = (Configtx.ConfigChild) childObjs[0];
        Configtx.ConfigChild writeChild = (Configtx.ConfigChild) childObjs[1];
        boolean childUpdate = (boolean) childObjs[2];

        //开始构造ConfigUpdate对象
        Configtx.ConfigUpdate.Builder configUpdateBuilder = Configtx.ConfigUpdate.newBuilder();
        configUpdateBuilder.setReadSet(readChild);
        configUpdateBuilder.setWriteSet(writeChild);

        return null;
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
            if (pendingPolicy.getModPolicy().equals(originalPolicy.getModPolicy()) && originalPolicy.getPolicy()
                    .equals(pendingPolicy.getPolicy())) {
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
            Configtx.ConfigPolicy originalPolicy = pendingMap.get(policyName);
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
            if (pendingValue.getModPolicy().equals(originalValue.getModPolicy()) && originalValue.getValue()
                    .equals(pendingValue.getValue())) {
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
            Configtx.ConfigValue originalValue = pendingMap.get(valueName);
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

    private static Object[] computeChildsMapUpdate(Map<String, Configtx.ConfigChild> originalMap, Map<String,
            Configtx.ConfigChild> pendingMap) {
        //TODO:如果有空的情况
        Map<String, Configtx.ConfigChild> readSet = new HashMap<String, Configtx.ConfigChild>();
        Map<String, Configtx.ConfigChild> writeSet = new HashMap<String, Configtx.ConfigChild>();
        Map<String, Configtx.ConfigChild> sameSet = new HashMap<String, Configtx.ConfigChild>();

        boolean hasMemberUpdate = false;

        //先循环originalMap
        Iterator<Map.Entry<String, Configtx.ConfigChild>> entries = originalMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Configtx.ConfigChild> entry = entries.next();
            String childName = entry.getKey();
            Configtx.ConfigChild originalChild = entry.getValue();

            //判断在新集合pendingMap有没有对应项
            Configtx.ConfigChild pendingChild = pendingMap.get(childName);
            if (pendingChild == null) {
                //该成员在新集合不存在，很可能被删除，说明集合成员项有更新
                hasMemberUpdate = true;
                continue;
            }

            //如果存在对应项，相同的并入sameSet，不同的并入writeSet
            Object[] childObjs = computeChildUpdate(originalChild, pendingChild);
            Configtx.ConfigChild readChild = (Configtx.ConfigChild) childObjs[0];
            Configtx.ConfigChild writeChild = (Configtx.ConfigChild) childObjs[1];
            boolean childUpdate = (boolean) childObjs[2];

            if (!childUpdate) {
                sameSet.put(childName, readChild);
            } else {
                readSet.put(childName, readChild);
                writeSet.put(childName, writeChild);
            }
        }

        //再循环pendingMap
        Iterator<Map.Entry<String, Configtx.ConfigChild>> pendingEntries = pendingMap.entrySet().iterator();
        while (pendingEntries.hasNext()) {
            Map.Entry<String, Configtx.ConfigChild> entry = pendingEntries.next();
            String childName = entry.getKey();
            Configtx.ConfigChild pendingChild = entry.getValue();

            //判断在原集合originalMap有没有对应项。有对应项的已经在前面处理过了，只需要再处理不存在的
            Configtx.ConfigChild originalChild = pendingMap.get(childName);
            if (originalChild == null) {
                //该成员在原集合不存在，很可能是新增项，说明集合成员项有更新
                hasMemberUpdate = true;

                //基于新集合创建另一个集合，只是版本为0.表示新增项
                Configtx.ConfigChild.Builder newVersionChildBuilder = Configtx.ConfigChild.newBuilder(pendingChild);
                newVersionChildBuilder.setVersion(0);
                Configtx.ConfigChild newVersionChild = newVersionChildBuilder.build();

                writeSet.put(childName, newVersionChild);
            }
        }

        return new Object[]{readSet, writeSet, sameSet, hasMemberUpdate};
    }


    private static Object[] computeChildUpdate(Configtx.ConfigChild originalChild, Configtx.ConfigChild pendingChild) {
        Object[] policiesObjs = computePoliciesMapUpdate(originalChild.getPoliciesMap(), pendingChild.getPoliciesMap());
        Object[] valuesObjs = computeValuesMapUpdate(originalChild.getValuesMap(), pendingChild.getValuesMap());
        Object[] childsObjs = computeChildsMapUpdate(originalChild.getChildsMap(), pendingChild.getChildsMap());

        Map<String, Configtx.ConfigPolicy> policiesReadSet = (Map<String, Configtx.ConfigPolicy>) policiesObjs[0];
        Map<String, Configtx.ConfigPolicy> policiesWriteSet = (Map<String, Configtx.ConfigPolicy>) policiesObjs[1];
        Map<String, Configtx.ConfigPolicy> policiesSameSet = (Map<String, Configtx.ConfigPolicy>) policiesObjs[2];
        boolean policiesMembersUpdate = (boolean) policiesObjs[3];

        Map<String, Configtx.ConfigValue> valuesReadSet = (Map<String, Configtx.ConfigValue>) valuesObjs[0];
        Map<String, Configtx.ConfigValue> valuesWriteSet = (Map<String, Configtx.ConfigValue>) valuesObjs[1];
        Map<String, Configtx.ConfigValue> valuesSameSet = (Map<String, Configtx.ConfigValue>) valuesObjs[2];
        boolean valuesMembersUpdate = (boolean) valuesObjs[3];

        Map<String, Configtx.ConfigChild> childsReadSet = (Map<String, Configtx.ConfigChild>) childsObjs[0];
        Map<String, Configtx.ConfigChild> childsWriteSet = (Map<String, Configtx.ConfigChild>) childsObjs[1];
        Map<String, Configtx.ConfigChild> childsSameSet = (Map<String, Configtx.ConfigChild>) childsObjs[2];
        boolean childsMembersUpdate = (boolean) childsObjs[3];

        if (!policiesMembersUpdate && !valuesMembersUpdate && !childsMembersUpdate && Objects.equals(originalChild
                .getModPolicy(), pendingChild.getModPolicy())) {
            //所有对象都未更新成员，且权限亦未更新
            if (policiesReadSet.size() <= 0 && policiesWriteSet.size() <= 0
                    && valuesReadSet.size() <= 0 && valuesWriteSet.size() <= 0
                    && childsReadSet.size() <= 0 && childsWriteSet.size() <= 0) {
                //完全未更改
                Configtx.ConfigChild resultSet = Configtx.ConfigChild.newBuilder().setVersion(originalChild.getVersion())
                        .build();
                Configtx.ConfigChild writeSet = Configtx.ConfigChild.newBuilder().setVersion(originalChild.getVersion())
                        .build();

                return new Object[]{resultSet, writeSet, false};
            } else {
                //部分更改
                Configtx.ConfigChild resultSet = Configtx.ConfigChild.newBuilder().setVersion(originalChild.getVersion())
                        .putAllPolicies(policiesReadSet).putAllValues(valuesReadSet).putAllChilds(childsReadSet).build();
                Configtx.ConfigChild writeSet = Configtx.ConfigChild.newBuilder().setVersion(originalChild.getVersion())
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
        Configtx.ConfigChild resultSet = Configtx.ConfigChild.newBuilder().setVersion(originalChild.getVersion())
                .putAllPolicies(policiesReadSet).putAllValues(valuesReadSet).putAllChilds(childsReadSet).build();
        Configtx.ConfigChild writeSet = Configtx.ConfigChild.newBuilder().setVersion(originalChild.getVersion() + 1)
                .putAllPolicies(policiesWriteSet).putAllValues(valuesWriteSet).putAllChilds(childsWriteSet).build();

        return new Object[]{resultSet, writeSet, true};
    }
}
