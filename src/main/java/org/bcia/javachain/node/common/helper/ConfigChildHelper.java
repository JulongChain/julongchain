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

import com.google.protobuf.Message;
import org.bcia.javachain.common.groupconfig.CapabilitiesValue;
import org.bcia.javachain.common.groupconfig.GroupConfigConstant;
import org.bcia.javachain.common.groupconfig.IConfigValue;
import org.bcia.javachain.common.groupconfig.MSPValue;
import org.bcia.javachain.common.policies.IConfigPolicy;
import org.bcia.javachain.common.policies.ImplicitMetaAnyPolicy;
import org.bcia.javachain.common.policies.ImplicitMetaMajorityPolicy;
import org.bcia.javachain.common.policies.SignaturePolicy;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.common.Policies;
import org.bcia.javachain.protos.msp.MspConfigPackage;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;

import java.util.HashMap;
import java.util.Map;

import static org.bcia.javachain.tools.configtxgen.entity.GenesisConfig.DEFAULT_ADMIN_PRINCIPAL;

/**
 * 类描述
 *
 * @author
 * @date 2018/3/9
 * @company Dingxuan
 */
public class ConfigChildHelper {
    /**
     * 为ConfigChild的构造器增加策略
     *
     * @param configChildBuilder
     * @param key
     * @param policy
     * @param modPolicy
     */
    public static void addPolicy(Configtx.ConfigChild.Builder configChildBuilder, String key, Policies.Policy policy,
                                 String modPolicy) {
        //首先构造ConfigPolicy
        Configtx.ConfigPolicy.Builder configPolicyBuilder = Configtx.ConfigPolicy.newBuilder();
        configPolicyBuilder.setPolicy(policy);
        configPolicyBuilder.setModPolicy(modPolicy);
        Configtx.ConfigPolicy configPolicy = configPolicyBuilder.build();

        //基于原ConfigChild构造新的ConfigChild
        configChildBuilder.putPolicies(key, configPolicy);
    }

    /**
     * 为ConfigChild的构造器增加Value
     *
     * @param configChildBuilder
     * @param key
     * @param message
     * @param modPolicy
     */
    public static void addValue(Configtx.ConfigChild.Builder configChildBuilder, String key, Message message,
                                String modPolicy) {
        //首先构造ConfigValue
        Configtx.ConfigValue.Builder configValueBuilder = Configtx.ConfigValue.newBuilder();
        configValueBuilder.setValue(message.toByteString());
        configValueBuilder.setModPolicy(modPolicy);
        Configtx.ConfigValue configValue = configValueBuilder.build();

        configChildBuilder.putValues(key, configValue);
    }

    /**
     * 获取默认的权限体系
     *
     * @return
     */
    public static Map<String, Configtx.ConfigPolicy> getDefaultImplicitMetaPolicy() {
        //首先创建默认的权限体系
        Map<String, Configtx.ConfigPolicy> defaultPolicies = new HashMap<String, Configtx.ConfigPolicy>();

        //构造管理员的内置策略(需要过半子策略被满足)
        Configtx.ConfigPolicy.Builder adminPolicyBuilder = Configtx.ConfigPolicy.newBuilder();
        adminPolicyBuilder.setPolicy(new ImplicitMetaMajorityPolicy(GroupConfigConstant.POLICY_ADMINS).value());
        adminPolicyBuilder.setModPolicy(GroupConfigConstant.POLICY_ADMINS);
        defaultPolicies.put(GroupConfigConstant.POLICY_ADMINS, adminPolicyBuilder.build());

        //构造可读人员的内置策略(任意子策略被满足即可)
        Configtx.ConfigPolicy.Builder readerPolicyBuilder = Configtx.ConfigPolicy.newBuilder();
        adminPolicyBuilder.setPolicy(new ImplicitMetaAnyPolicy(GroupConfigConstant.POLICY_READERS).value());
        adminPolicyBuilder.setModPolicy(GroupConfigConstant.POLICY_ADMINS);
        defaultPolicies.put(GroupConfigConstant.POLICY_READERS, readerPolicyBuilder.build());

        //构造可写人员的内置策略(任意子策略被满足即可)
        Configtx.ConfigPolicy.Builder writerPolicyBuilder = Configtx.ConfigPolicy.newBuilder();
        adminPolicyBuilder.setPolicy(new ImplicitMetaAnyPolicy(GroupConfigConstant.POLICY_WRITERS).value());
        adminPolicyBuilder.setModPolicy(GroupConfigConstant.POLICY_ADMINS);
        defaultPolicies.put(GroupConfigConstant.POLICY_WRITERS, writerPolicyBuilder.build());

        return defaultPolicies;
    }

    /**
     * 构造应用级别的子树
     *
     * @param application
     * @return
     */
    public static Configtx.ConfigChild buildApplicationChild(GenesisConfig.Application application) {
        //构建最终的应用ConfigChild
        Configtx.ConfigChild.Builder applicationChildBuilder = Configtx.ConfigChild.newBuilder();

        //填充默认的权限体系
        Map<String, Configtx.ConfigPolicy> defaultPolicies = ConfigChildHelper.getDefaultImplicitMetaPolicy();
        applicationChildBuilder.putAllPolicies(defaultPolicies);

        //填充能力集
        if (application.getCapabilities() != null && !application.getCapabilities().isEmpty()) {
            IConfigValue configValue = new CapabilitiesValue(application.getCapabilities());
            ConfigChildHelper.addValue(applicationChildBuilder, configValue.key(), configValue.value(), GroupConfigConstant.POLICY_ADMINS);
        }

        //填充子树信息
        if (application.getOrganizations() != null && application.getOrganizations().length > 0) {
            for (GenesisConfig.Organization org : application.getOrganizations()) {
                applicationChildBuilder.putChilds(org.getName(), buildOrgChild(org));
            }
        }

        //填充更改策略人
        applicationChildBuilder.setModPolicy(GroupConfigConstant.POLICY_ADMINS);

        return applicationChildBuilder.build();
    }

    /**
     * 构造组织级别的子树
     *
     * @param org
     * @return
     */
    public static Configtx.ConfigChild buildOrgChild(GenesisConfig.Organization org) {
        //构建最终的应用ConfigChild
        Configtx.ConfigChild.Builder orgChildBuilder = Configtx.ConfigChild.newBuilder();

        //填充签名策略
        addSignaturePolicyDefaults(orgChildBuilder, org.getId(), !DEFAULT_ADMIN_PRINCIPAL.equals(org
                .getAdminPrincipal()));

        //填充MSP信息
        MspConfigPackage.MSPConfig mspConfig = MockMSPManager.getVerifyingMspConfig(org.getMspDir(), org.getId(), org.getMspType());
        IConfigValue mspValue = new MSPValue(mspConfig);
        addValue(orgChildBuilder, mspValue.key(), mspValue.value(), GroupConfigConstant.POLICY_ADMINS);

        //填充更改策略人
        orgChildBuilder.setModPolicy(GroupConfigConstant.POLICY_ADMINS);

        return orgChildBuilder.build();
    }

    /**
     * 增加默认的签名策略
     *
     * @param configChildBuilder
     * @param mspId
     * @param devMode
     */
    public static void addSignaturePolicyDefaults(Configtx.ConfigChild.Builder configChildBuilder, String mspId, boolean
            devMode) {
        Policies.SignaturePolicyEnvelope policyEnvelope = null;
        if (devMode) {
            policyEnvelope = MockCauthdsl.signedByMspMember(mspId);
        } else {
            policyEnvelope = MockCauthdsl.signedByMspAdmin(mspId);
        }

        IConfigPolicy adminPolicy = new SignaturePolicy(GroupConfigConstant.POLICY_ADMINS, policyEnvelope);
        addPolicy(configChildBuilder, adminPolicy.key(), adminPolicy.value(), GroupConfigConstant.POLICY_ADMINS);

        IConfigPolicy readerPolicy = new SignaturePolicy(GroupConfigConstant.POLICY_READERS, MockCauthdsl.signedByMspMember(mspId));
        addPolicy(configChildBuilder, readerPolicy.key(), readerPolicy.value(), GroupConfigConstant.POLICY_ADMINS);

        IConfigPolicy writerPolicy = new SignaturePolicy(GroupConfigConstant.POLICY_WRITERS, MockCauthdsl.signedByMspMember(mspId));
        addPolicy(configChildBuilder, writerPolicy.key(), writerPolicy.value(), GroupConfigConstant.POLICY_ADMINS);
    }
}
