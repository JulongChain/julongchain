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
package org.bcia.julongchain.tools.configtxgen.helper;

import com.alibaba.fastjson.JSON;
import org.bcia.julongchain.common.exception.ConfigtxToolsException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.genesis.GenesisBlockFactory;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.common.util.proto.EnvelopeHelper;
import org.bcia.julongchain.common.util.proto.ProtoUtils;
import org.bcia.julongchain.node.common.helper.ConfigTreeHelper;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.node.Configuration;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfigFactory;

import java.io.File;
import java.io.IOException;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/06/06
 * @company Dingxuan
 */
public class ConfigtxHelper {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ConfigtxHelper.class);

    public static void doOutputBlock(GenesisConfig.Profile profile, String groupId, String outputBlock)
            throws ValidateException, ConfigtxToolsException {
        ValidateUtils.isNotNull(profile, "profile can not be null");
        ValidateUtils.isNotNull(profile.getConsortiums(), "profile.getConsortiums can not be null");

        Configtx.ConfigTree groupTree = ConfigTreeHelper.buildGroupTree(profile);
        try {
            Common.Block genesisBlock = new GenesisBlockFactory(groupTree).getGenesisBlock(groupId);

            org.apache.commons.io.FileUtils.forceMkdirParent(new File(outputBlock));
            FileUtils.writeFileBytes(outputBlock, genesisBlock.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConfigtxToolsException(e);
        }
    }

    public static void doOutputGroupCreateTx(GenesisConfig.Profile profile, String groupId,
                                             String outputGroupCreateTx) throws ConfigtxToolsException,
            ValidateException {
        ValidateUtils.isNotNull(profile, "profile can not be null");
        try {
            Common.Envelope envelope = EnvelopeHelper.makeGroupCreateTx(groupId, null, null, profile);

            org.apache.commons.io.FileUtils.forceMkdirParent(new File(outputGroupCreateTx));
            FileUtils.writeFileBytes(outputGroupCreateTx, envelope.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConfigtxToolsException(e);
        }
    }

    public static void doOutputAnchorNodesUpdate(GenesisConfig.Profile profile, String groupId,
                                                 String outputAnchorNodesUpdate, String asOrg) throws ValidateException,
            ConfigtxToolsException {
        ValidateUtils.isNotNull(profile, "profile can not be null");
        ValidateUtils.isNotNull(profile.getApplication(), "profile.getApplication can not be null");
        ValidateUtils.isNotNull(profile.getApplication().getOrganizations(), "profile.getApplication.getOrganizations" +
                " can not be null");

        GenesisConfig.Organization asOrganization = null;
        GenesisConfig.Organization[] organizations = profile.getApplication().getOrganizations();
        for (int i = 0; i < organizations.length; i++) {
            if (asOrg.equals(organizations[i].getName())) {
                asOrganization = organizations[i];
                break;
            }
        }
        ValidateUtils.isNotNull(asOrganization, "asOrganization can not be null");
        Configtx.ConfigUpdate configUpdate = EnvelopeHelper.makeConfigUpdate(groupId, asOrg,
                parseAnchorNodes(asOrganization.getAnchorNodes()));

        Configtx.ConfigUpdateEnvelope.Builder configUpdateEnvelopeBuilder = Configtx.ConfigUpdateEnvelope.newBuilder();
        configUpdateEnvelopeBuilder.setConfigUpdate(configUpdate.toByteString());
        Configtx.ConfigUpdateEnvelope configUpdateEnvelope = configUpdateEnvelopeBuilder.build();

        Common.Envelope envelope = EnvelopeHelper.buildSignedEnvelope(Common.HeaderType.CONFIG_UPDATE_VALUE, 0,
                groupId, null, configUpdateEnvelope, 0);
        try {
            org.apache.commons.io.FileUtils.forceMkdirParent(new File(outputAnchorNodesUpdate));
            FileUtils.writeFileBytes(outputAnchorNodesUpdate, envelope.toByteArray());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConfigtxToolsException(e);
        }
    }

    private static Configuration.AnchorNode[] parseAnchorNodes(GenesisConfig.AnchorNode[] anchorNodes) {
        Configuration.AnchorNode[] result = null;

        if (anchorNodes != null && anchorNodes.length > 0) {
            result = new Configuration.AnchorNode[anchorNodes.length];
            for (int i = 0; i < anchorNodes.length; i++) {
                GenesisConfig.AnchorNode anchorNode = anchorNodes[i];

                Configuration.AnchorNode.Builder builder = Configuration.AnchorNode.newBuilder();
                builder.setHost(anchorNode.getHost());
                builder.setPort(anchorNode.getPort());

                result[i] = builder.build();
            }
        }

        return result;
    }

    public static void doInspectBlock(String inspectBlock) throws ConfigtxToolsException {
        try {
            byte[] fileBytes = FileUtils.readFileBytes(inspectBlock);
            Common.Block block = Common.Block.parseFrom(fileBytes);
            log.info("Get a block: \n" + JSON.toJSONString(block, true));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConfigtxToolsException(e);
        }
    }

    public static void doInspectGroupCreateTx(String inspectGroupCreateTx) throws ConfigtxToolsException {
        try {
            byte[] fileBytes = FileUtils.readFileBytes(inspectGroupCreateTx);
            Common.Envelope envelope = Common.Envelope.parseFrom(fileBytes);
            log.info("Get a envelope: \n" + JSON.toJSONString(envelope, true));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConfigtxToolsException(e);
        }
    }

    public static void doPrintOrganization(String printOrg) throws ValidateException {
        GenesisConfig genesisConfig = GenesisConfigFactory.getGenesisConfig();

        GenesisConfig.Organization[] organizations = genesisConfig.getOrganizations();
        if (organizations == null || organizations.length <= 0) {
            throw new ValidateException("organizations is empty");
        }

        for (GenesisConfig.Organization org : organizations) {
            if (printOrg.equals(org.getName())) {
                Configtx.ConfigTree consenterOrgTree = ConfigTreeHelper.buildConsenterOrgTree(org);
                log.info("Get a consenterOrg: \n");
                ProtoUtils.printMessageJson(consenterOrgTree);

                return;
            }
        }
    }
}
