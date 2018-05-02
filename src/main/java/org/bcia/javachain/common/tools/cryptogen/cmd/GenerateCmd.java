/**
 * Copyright BCIA. All Rights Reserved.
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

package org.bcia.javachain.common.tools.cryptogen.cmd;

import org.apache.commons.cli.*;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.tools.cryptogen.bean.Config;
import org.bcia.javachain.common.tools.cryptogen.bean.OrgSpec;
import org.bcia.javachain.consenter.util.LoadYaml;

import static org.bcia.javachain.common.tools.cryptogen.cmd.Util.*;

/**
 * @author chenhao, liuxifeng
 * @date 2018/4/4
 * @company Excelsecu
 */
public class GenerateCmd implements ICryptoGenCmd {


    private static JavaChainLog log = JavaChainLogFactory.getLog(GenerateCmd.class);

    private String outputDir;

    private String configFile;

    @Override
    public void execCmd(String[] args) {

        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption("output", true, "The output directory in which to place artifacts");
        options.addOption("config", true, "The configuration template to use");
        options.addOption("help", false, "print this message");

        try {
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.hasOption("help")) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("generate", options);
            }
            outputDir = commandLine.getOptionValue("output", "crypto-config");
            configFile = commandLine.getOptionValue("config", "crypto-config.yaml");
            generate();

        } catch (ParseException e) {
            log.error(e.getMessage());
        }
    }

    private void generate() {
        Config config = null;
        try {
            config = Util.loadAs(configFile, Config.class);
        } catch (JavaChainException e) {
            log.error(e.getMessage());
            System.exit(-1);
        }
        initConfig(config);
        for (OrgSpec orgSpec : config.getPeerOrgs()) {
            try {
                renderOrgSpec(orgSpec, "peer");
            } catch (JavaChainException e) {
                log.error("Error processing peer configuration: " + e.getMessage());
                System.exit(-1);
            }
            generatePeerOrg(outputDir, orgSpec);
        }
        for (OrgSpec orgSpec : config.getConsenterOrgs()) {
            try {
                renderOrgSpec(orgSpec, "consenter");
            } catch (JavaChainException e) {
                log.error("Error processing consenter configuration: " + e.getMessage());
                System.exit(-1);
            }
            generateConstenerOrgs(outputDir, orgSpec);
        }
    }

    private void initConfig(Config config) {



    }
}
