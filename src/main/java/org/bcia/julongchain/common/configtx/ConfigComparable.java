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
package org.bcia.julongchain.common.configtx;

import org.bcia.julongchain.protos.common.Configtx;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * 针对T对象的比对器(T为ConfigTree/ConfigValue/ConfigPolicy的一种)
 *
 * @author zhouhui
 * @date 2018/4/24
 * @company Dingxuan
 */
public class ConfigComparable<T> {
    private String key;
    private String[] path;
    private T t;

    public ConfigComparable(String key, String[] path, T t) {
        this.key = key;
        this.path = path;
        this.t = t;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof ConfigComparable)) {
            return false;
        }

        ConfigComparable<T> other = (ConfigComparable<T>) obj;
        T otherT = other.getT();
        if (t instanceof Configtx.ConfigTree && otherT instanceof Configtx.ConfigTree) {
            return equalConfigTree((Configtx.ConfigTree) t, (Configtx.ConfigTree) otherT);
        }

        if (t instanceof Configtx.ConfigValue && otherT instanceof Configtx.ConfigValue) {
            return equalConfigValue((Configtx.ConfigValue) t, (Configtx.ConfigValue) otherT);
        }

        if (t instanceof Configtx.ConfigPolicy && otherT instanceof Configtx.ConfigPolicy) {
            return equalConfigPolicy((Configtx.ConfigPolicy) t, (Configtx.ConfigPolicy) otherT);
        }

        return false;
    }

    /**
     * 比较两个ConfigTree对象是否相等
     *
     * @param t
     * @param otherT
     * @return
     */
    private boolean equalConfigTree(Configtx.ConfigTree t, Configtx.ConfigTree otherT) {
        if (t == null || otherT == null) {
            return false;
        }

        if (!Objects.equals(t.getVersion(), otherT.getVersion())
                || !Objects.equals(t.getModPolicy(), otherT.getModPolicy())) {
            return false;
        }

        return subsetOf(t.getChildsMap(), otherT.getChildsMap())
                && subsetOf(otherT.getChildsMap(), t.getChildsMap())
                && subsetOf(t.getValuesMap(), otherT.getValuesMap())
                && subsetOf(otherT.getValuesMap(), t.getValuesMap())
                && subsetOf(t.getPoliciesMap(), otherT.getPoliciesMap())
                && subsetOf(otherT.getPoliciesMap(), t.getPoliciesMap());
    }

    private boolean equalConfigValue(Configtx.ConfigValue t, Configtx.ConfigValue otherT) {
        if (t == null || otherT == null) {
            return false;
        }

        if (!Objects.equals(t.getVersion(), otherT.getVersion())
                || !Objects.equals(t.getModPolicy(), otherT.getModPolicy())) {
            return false;
        }

        return Arrays.equals(t.getValue().toByteArray(), otherT.getValue().toByteArray());
    }

    private boolean equalConfigPolicy(Configtx.ConfigPolicy t, Configtx.ConfigPolicy otherT) {
        if (t == null || otherT == null) {
            return false;
        }

        if (!Objects.equals(t.getVersion(), otherT.getVersion())
                || !Objects.equals(t.getModPolicy(), otherT.getModPolicy())) {
            return false;
        }

        if (t.getPolicy() == null || otherT.getPolicy() == null) {
            return t.getPolicy() == otherT.getPolicy();
        }

        return t.getPolicy().getType() == otherT.getPolicy().getType()
                && Arrays.equals(t.getPolicy().getValue().toByteArray(), otherT.getPolicy().getValue().toByteArray());
    }

    /**
     * A集合是否是B集合的子集
     *
     * @param aMap
     * @param bMap
     * @return
     */
    private boolean subsetOf(Map<String, ?> aMap, Map<String, ?> bMap) {
        if (aMap.size() <= 0) {
            //空集合是任意集合的子集
            return true;
        }

        if (aMap.size() > bMap.size()) {
            //如果A集合元素比B集合元素还多，必定不是B集合的子集
            return false;
        }

        for (String key : aMap.keySet()) {
            //如果对A集合的每一个元素来说，都存在于B集合，则说明A是B的子集，否则非子集
            if (!bMap.containsKey(key)) {
                return false;
            }
        }

        return true;
    }

    public String getKey() {
        return key;
    }

    public String[] getPath() {
        return path;
    }

    public T getT() {
        return t;
    }

    public long getVersion() {
        if (t instanceof Configtx.ConfigTree) {
            return ((Configtx.ConfigTree) t).getVersion();
        }

        if (t instanceof Configtx.ConfigValue) {
            return ((Configtx.ConfigValue) t).getVersion();
        }

        if (t instanceof Configtx.ConfigPolicy) {
            return ((Configtx.ConfigPolicy) t).getVersion();
        }

        return 0;
    }

    public String getModPolicy() {
        if (t instanceof Configtx.ConfigTree) {
            return ((Configtx.ConfigTree) t).getModPolicy();
        }

        if (t instanceof Configtx.ConfigValue) {
            return ((Configtx.ConfigValue) t).getModPolicy();
        }

        if (t instanceof Configtx.ConfigPolicy) {
            return ((Configtx.ConfigPolicy) t).getModPolicy();
        }

        return null;
    }
}
