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
import org.bcia.javachain.common.tools.cryptogen.CaHelper;
import org.bcia.javachain.common.tools.cryptogen.CspHelper;
import org.bcia.javachain.common.tools.cryptogen.MspHelper;
import org.bcia.javachain.common.tools.cryptogen.bean.Config;
import org.bcia.javachain.common.tools.cryptogen.bean.NodeSpec;
import org.bcia.javachain.common.tools.cryptogen.bean.OrgSpec;
import org.bcia.javachain.csp.intfs.IKey;
import org.bouncycastle.asn1.x509.Certificate;

import java.io.File;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static org.bcia.javachain.common.tools.cryptogen.cmd.Util.*;

/**
 * @author chenhao, liuxifeng
 * @date 2018/4/4
 * @company Excelsecu
 */
public class ExtendCmd implements ICryptoGenCmd {


    private static JavaChainLog log = JavaChainLogFactory.getLog(ExtendCmd.class);

    //the value of "input" commandline option
    private String inputDir;

    //the value of "configFile" commandline option
    private String configFile;

    private void extendConsenterOrg(OrgSpec orgSpec) throws JavaChainException {

        String orgName = orgSpec.getDomain();
        String orgDir = Paths.get(inputDir, "consenterOrganizations", orgName).toString();
        String peersDir = Paths.get(orgDir, "consenters").toString();
        String usersDir = Paths.get(orgDir, "users").toString();
        String caDir = Paths.get(orgDir, "ca").toString();
        String tlscaDir = Paths.get(orgDir, "tlsca").toString();

        File file = new File(orgDir);
        if (!file.exists()) {
            generateConstenerOrgs(inputDir, orgSpec);
            return;
        }
        CaHelper signCA = getCA(caDir, orgSpec, orgSpec.getCa().getCommonName());
        CaHelper tlsCA = getCA(tlscaDir, orgSpec, "tls" + orgSpec.getCa().getCommonName());

        generateNodes(peersDir, orgSpec.getSpecs(), signCA, tlsCA, MspHelper.PEER, orgSpec.isEnableNodeOUs());

        NodeSpec adminUser = new NodeSpec();
        adminUser.setCommonName(ADMIN_BASE_NAME + "@" + orgName);

        copyAllAdminCerts(usersDir, peersDir, orgName, orgSpec, adminUser);


    }

    private void extendPeerOrg(OrgSpec orgSpec) throws JavaChainException {

        String orgName = orgSpec.getDomain();
        String orgDir = Paths.get(inputDir, "peerOrganizations", orgName).toString();

        File file = new File(orgDir);
        if (!file.exists()) {
            generatePeerOrg(inputDir, orgSpec);
            return;
        }

        String peersDir = Paths.get(orgDir, "peers").toString();
        String usersDir = Paths.get(orgDir, "users").toString();
        String caDir = Paths.get(orgDir, "ca").toString();
        String tlscaDir = Paths.get(orgDir, "tlsca").toString();


        CaHelper signCA = getCA(caDir, orgSpec, orgSpec.getCa().getCommonName());
        CaHelper tlsCA = getCA(tlscaDir, orgSpec, "tls" +orgSpec.getCa().getCommonName());

        generateNodes(peersDir, orgSpec.getSpecs(), signCA, tlsCA, MspHelper.PEER, orgSpec.isEnableNodeOUs());

        NodeSpec adminUser = new NodeSpec();
        adminUser.setCommonName(ADMIN_BASE_NAME + "@" + orgName);

        copyAllAdminCerts(usersDir, peersDir, orgName, orgSpec, adminUser);
        List<NodeSpec> users = new ArrayList<>();

        int userCount =orgSpec.getUsers().getCount();
        for (int i = 1; i <= userCount; i++) {

            NodeSpec user = new NodeSpec();
            user.setCommonName(USER_BASE_NAME + i + "@" + orgName);
            users.add(user);
        }

        generateNodes(usersDir, users, signCA, tlsCA, MspHelper.CLIENT, orgSpec.isEnableNodeOUs());
    }


    private CaHelper getCA(String caDir, OrgSpec spec, String name) throws JavaChainException {
        IKey privKey = CspHelper.loadPrivateKey(caDir);

        Certificate cert = CaHelper.loadCertificateSM2(caDir);
        NodeSpec ca = spec.getCa();
        return new CaHelper(
                name,
                ca.getCountry(),
                ca.getProvince(),
                ca.getLocality(),
                ca.getOrganizationUnit(),
                ca.getStreetAddress(),
                ca.getPostalCode(),
                privKey,
                cert);
    }


    @Override
    public void execCmd(String[] args) throws JavaChainException {

        Options options = new Options();
        options.addOption("input", true, "the input directory in which existing network place");
        options.addOption("config", true, "the configuration template to use");
        options.addOption("help", false, "print this message");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("help")) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("extend", options);
            }
            inputDir = commandLine.getOptionValue("input", "crypto-config");
            configFile = commandLine.getOptionValue("config", "crypto-config.yaml");
            extend();
        } catch (ParseException e) {
            throw new JavaChainException(e.getMessage());
        }

    }

    private void extend() throws JavaChainException {

        Config config = Util.loadAs(configFile, Config.class);

        for (OrgSpec orgSpec : config.getPeerOrgs()) {
            try {
                renderOrgSpec(orgSpec, "peer");
            } catch (JavaChainException e) {
                log.error("Error processing peer configuration: " + e.getMessage());
                System.exit(-1);
            }
            extendPeerOrg(orgSpec);
        }
        for (OrgSpec orgSpec : config.getConsenterOrgs()) {
            try {
                renderOrgSpec(orgSpec, "consenter");
            } catch (JavaChainException e) {
                log.error("Error processing consenter configuration: " + e.getMessage());
                System.exit(-1);
            }
            extendConsenterOrg(orgSpec);
        }
    }
}
