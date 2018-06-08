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
package org.bcia.julongchain.tools.configtxgen.cmd;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.ConfigtxToolsException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfigFactory;
import org.bcia.julongchain.tools.configtxgen.helper.ConfigtxHelper;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/06/06
 * @company Dingxuan
 */
public class ConfigtxGenCmd implements IConfigtxGenCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ConfigtxGenCmd.class);

    //参数：groupId
    private static final String ARG_GROUP_ID = "g";
    private static final String ARG_PROFILE = "profile";
    private static final String ARG_OUTPUT_BLOCK = "outputBlock";
    private static final String ARG_OUTPUT_GROUP_CREATE_TX = "outputGroupCreateTx";
    private static final String ARG_OUTPUT_ANCHOR_NODES_UPDATE = "outputAnchorNodesUpdate";

    private static final String ARG_INSPECTBLOCK = "inspectBlock";
    private static final String ARG_INSPECT_GROUP_CREATE_TX = "inspectGroupCreateTx";

    private static final String ARG_AS_ORGANIZATION = "asOrg";
    private static final String ARG_PRINT_ORGANIZATION = "printOrg";

    @Override
    public void execCmd(String[] args) throws ParseException, ConfigtxToolsException, ValidateException {

        Options options = new Options();
        options.addOption(ARG_GROUP_ID, true, "Input group id");
        options.addOption(ARG_PROFILE, true, "");
        options.addOption(ARG_OUTPUT_BLOCK, true, "");
        options.addOption(ARG_OUTPUT_GROUP_CREATE_TX, true, "");
        options.addOption(ARG_OUTPUT_ANCHOR_NODES_UPDATE, true, "");

        options.addOption(ARG_INSPECTBLOCK, true, "");
        options.addOption(ARG_INSPECT_GROUP_CREATE_TX, true, "");

        options.addOption(ARG_AS_ORGANIZATION, true, "");
        options.addOption(ARG_PRINT_ORGANIZATION, true, "");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "";

        //-----------------------------------解析参数值-------------------------------//
        //解析出群组ID
        String groupId = null;
        if (cmd.hasOption(ARG_GROUP_ID)) {
            groupId = cmd.getOptionValue(ARG_GROUP_ID, defaultValue);
            log.info("GroupId-----$" + groupId);
        }

        String profile = null;
        if (cmd.hasOption(ARG_PROFILE)) {
            profile = cmd.getOptionValue(ARG_PROFILE, defaultValue);
            log.info("profile-----$" + profile);
        }

        String outputBlock = null;
        if (cmd.hasOption(ARG_OUTPUT_BLOCK)) {
            outputBlock = cmd.getOptionValue(ARG_OUTPUT_BLOCK, defaultValue);
            log.info("OutputBlock-----$" + outputBlock);
        }

        String outputGroupCreateTx = null;
        if (cmd.hasOption(ARG_OUTPUT_GROUP_CREATE_TX)) {
            outputGroupCreateTx = cmd.getOptionValue(ARG_OUTPUT_GROUP_CREATE_TX, defaultValue);
            log.info("OutputGroupCreateTx-----$" + outputGroupCreateTx);
        }

        String outputAnchorNodesUpdate = null;
        if (cmd.hasOption(ARG_OUTPUT_ANCHOR_NODES_UPDATE)) {
            outputAnchorNodesUpdate = cmd.getOptionValue(ARG_OUTPUT_ANCHOR_NODES_UPDATE, defaultValue);
            log.info("OutputGroupCreateTx-----$" + outputAnchorNodesUpdate);
        }

        String asOrg = null;
        if (cmd.hasOption(ARG_AS_ORGANIZATION)) {
            asOrg = cmd.getOptionValue(ARG_AS_ORGANIZATION, defaultValue);
            log.info("asOrg-----$" + asOrg);
        }

        String inspectBlock = null;
        if (cmd.hasOption(ARG_INSPECTBLOCK)) {
            inspectBlock = cmd.getOptionValue(ARG_INSPECTBLOCK, defaultValue);
            log.info("inspectBlock-----$" + inspectBlock);
        }

        String inspectGroupCreateTx = null;
        if (cmd.hasOption(ARG_INSPECT_GROUP_CREATE_TX)) {
            inspectGroupCreateTx = cmd.getOptionValue(ARG_INSPECT_GROUP_CREATE_TX, defaultValue);
            log.info("inspectGroupCreateTx-----$" + inspectGroupCreateTx);
        }

        String printOrg = null;
        if (cmd.hasOption(ARG_PRINT_ORGANIZATION)) {
            printOrg = cmd.getOptionValue(ARG_PRINT_ORGANIZATION, defaultValue);
            log.info("printOrg-----$" + printOrg);
        }

        //-----------------------------------业务逻辑--------------------------------//
        if (StringUtils.isNotBlank(outputBlock)) {
            ValidateUtils.isNotBlank(profile, "profile can not be empty");
            GenesisConfig.Profile profileConfig = GenesisConfigFactory.getGenesisConfig().getCompletedProfile(profile);
            ConfigtxHelper.doOutputBlock(profileConfig, groupId, outputBlock);
            return;
        }

        if (StringUtils.isNotBlank(outputGroupCreateTx)) {
            ValidateUtils.isNotBlank(profile, "profile can not be empty");
            GenesisConfig.Profile profileConfig = GenesisConfigFactory.getGenesisConfig().getCompletedProfile(profile);
            ConfigtxHelper.doOutputGroupCreateTx(profileConfig, groupId, outputGroupCreateTx);
            return;
        }

        if (StringUtils.isNotBlank(outputAnchorNodesUpdate)) {
            ValidateUtils.isNotBlank(profile, "profile can not be empty");
            ValidateUtils.isNotBlank(asOrg, "asOrg can not be empty");
            GenesisConfig.Profile profileConfig = GenesisConfigFactory.getGenesisConfig().getCompletedProfile(profile);
            ConfigtxHelper.doOutputAnchorNodesUpdate(profileConfig, groupId, outputAnchorNodesUpdate, asOrg);
            return;
        }

        if (StringUtils.isNotBlank(inspectBlock)) {
            ConfigtxHelper.doInspectBlock(inspectBlock);
            return;
        }

        if (StringUtils.isNotBlank(inspectGroupCreateTx)) {
            ConfigtxHelper.doInspectGroupCreateTx(inspectGroupCreateTx);
            return;
        }

        if (StringUtils.isNotBlank(printOrg)) {
            ConfigtxHelper.doPrintOrganization(printOrg);
            return;
        }

    }
}
