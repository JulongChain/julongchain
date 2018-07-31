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

package org.bcia.julongchain.common.tools.cryptogen.cmd;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.tools.cryptogen.CaHelper;
import org.bcia.julongchain.common.tools.cryptogen.FileUtil;
import org.bcia.julongchain.common.tools.cryptogen.MspHelper;
import org.bcia.julongchain.common.tools.cryptogen.bean.*;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 密码材料生成工具类
 *
 * @author chenhao, liuxifeng
 * @date 2018/4/17
 * @company Excelsecu
 */
@SuppressWarnings("WeakerAccess")
public class Util {
    private static final String defaultHostnameTemplate = "{{.Prefix}}{{.Index}}";
    private static final String defaultCNTemplate = "{{.Hostname}}.{{.Domain}}";
    static String ADMIN_BASE_NAME = "Admin";
    static String USER_BASE_NAME = "User";

    private static JavaChainLog log = JavaChainLogFactory.getLog(Util.class);

    //copy the admin cert to each of the org's node's or consenter's MSP admincerts
    static void copyAllAdminCerts(String usersDir, String dstDir, String orgName, OrgSpec orgSpec, NodeSpec adminUser) {
        String dir = dstDir.contains("nodes") ? "nodes" : "consenter";
        for (NodeSpec spec : orgSpec.getSpecs()) {
            try {
                copyAdminCert(usersDir, Paths.get(dstDir, spec.getCommonName(), "msp", "admincerts").toString(), adminUser.getCommonName());
            } catch (JavaChainException e) {
                log.error("Error copying admin cert for org {} {} {}", orgName, dir, spec.getCommonName());
                log.error(e.getMessage());
                System.exit(1);
            }
        }
    }

    private static void copyAdminCert(String usersDir, String adminCertDir, String adminUserName) throws JavaChainException {
        File file = new File(Paths.get(adminCertDir, adminUserName + "-cert.pem").toString());
        if (file.exists()) {
            return;
        }
        FileUtil.removeAll(adminCertDir);

        // recreate the admincerts directory
        File adminCertFile = new File(adminCertDir);
        FileUtil.mkdirAll(Paths.get(adminCertFile.getAbsolutePath()));
        try {
            File srcFile = new File(Paths.get(usersDir, adminUserName, "msp", "signcerts", adminUserName + "-cert.pem").toString());
            File dstFile = new File(Paths.get(adminCertDir, adminUserName + "-cert.pem").toString());
            FileUtils.copyFile(srcFile, dstFile);
        } catch (Exception e) {
            throw new JavaChainException();
        }
    }


    static void renderOrgSpec(OrgSpec orgSpec, String prefix) throws JavaChainException {

        NodeTemplate nodeTemplate = orgSpec.getTemplate();
        int tempCount = nodeTemplate.getCount();
        // First process all of our template nodes
        for (int i = 0; i < tempCount; i++) {
            HostNameData data = new HostNameData(prefix, i + nodeTemplate.getStart(), orgSpec.getDomain());
            String hostName = parseTemplateWithDefault(nodeTemplate.getHostname(), defaultHostnameTemplate, data);

            NodeSpec nodeSpec = new NodeSpec();
            nodeSpec.setCommonName(hostName);
            nodeSpec.setSans(nodeTemplate.getSans());
            List<NodeSpec> specs = orgSpec.getSpecs();
            specs.add(nodeSpec);
            orgSpec.setSpecs(specs);
        }
        // Touch up all general node-specs to add the domain
        List<NodeSpec> nodeSpec =orgSpec.getSpecs();
        int size = nodeSpec.size();
        for (int i = 0; i < size; i++) {
            renderNodeSpec(orgSpec.getDomain(), nodeSpec.get(i));
            nodeSpec.set(i, nodeSpec.get(i));
        }
        // Process the CaHelper node-spec in the same manner
        NodeSpec ca =orgSpec.getCa();
        if (ca == null) {
            ca = new NodeSpec();
            orgSpec.setCa(ca);
        }
        String hostName = ca.getHostname();
        if (hostName.length() == 0) {
            ca.setHostname("ca");
        }
        renderNodeSpec(orgSpec.getDomain(), orgSpec.getCa());
    }

    private static void renderNodeSpec(String domain, NodeSpec spec) {

        SpecData data = new SpecData();
        data.setHostname(spec.getHostname());
        data.setDomain(domain);

        //Process our CommonName
        String cn = parseTemplateWithDefault(spec.getCommonName(), defaultCNTemplate, data);
        spec.setCommonName(cn);
        data.setCommonName(cn);

        // Save off our original, unprocessed sans entries
        List<String> originSans = spec.getSans();
        if (originSans == null) {
            originSans = new ArrayList<>();
            spec.setSans(originSans);
        }

        // Set our implicit sans entries for CN/Hostname
        List<String> newSans = new ArrayList<>();
        newSans.add(cn);
        String hostName = spec.getHostname();
        if (hostName != null && hostName.length() != 0) {
            newSans.add(spec.getHostname());
        }
        spec.setSans(newSans);

        // Finally, process any remaining sans entries
        for (String orgSan : originSans) {
            String san = parseTemplate(orgSan, data);
            spec.getSans().add(san);
        }
    }

    private static String parseTemplateWithDefault(String input, String defaultInput, Object data) {

        // Use the default if the input is an empty string
        if(StringUtils.isEmpty(input)) {
            input = defaultInput;
        }
        return parseTemplate(input, data);
    }


    private static String parseTemplate(String input, Object data) {


        if (data instanceof HostNameData) {
            String prefix = ((HostNameData) data).getPrefix();
            String index = ((HostNameData) data).getIndex() + "";
            return input.replace("{{.Prefix}}", prefix).replace("{{.Index}}", index);

        } else if (data instanceof SpecData) {

            String domain = ((SpecData) data).getDomain();
            String hostName = ((SpecData) data).getHostname();
            return input.replace("{{.Hostname}}", hostName).replace("{{.Domain}}", domain);
        }
        return null;
    }


    static void generateNodeOrg(String baseDir, OrgSpec orgSpec) {
        String orgName = orgSpec.getDomain();
        System.out.println(orgName);
        // generate CAs
        String orgDir = Paths.get(baseDir, "nodeOrganizations", orgName).toString();
        String caDir = Paths.get(orgDir, "ca").toString();
        String tlsCADir = Paths.get(orgDir, "tlsca").toString();
        String mspDir = Paths.get(orgDir, "msp").toString();
        String nodeDir = Paths.get(orgDir, "nodes").toString();
        String userDir = Paths.get(orgDir, "users").toString();
        String adminCertDir = Paths.get(mspDir, "admincerts").toString();

        CaHelper signCA;
        CaHelper tlsCA;

        // generate signing CaHelper
        NodeSpec caSpec = orgSpec.getCa();
        signCA = Util.generateCA(caDir, orgName, caSpec.getCommonName(), caSpec);

        // generate TLS CaHelper
        tlsCA = Util.generateCA(tlsCADir, orgName, "tls" + caSpec.getCommonName(), caSpec);

        try {
            MspHelper.generateVerifyingMSP(mspDir, signCA, tlsCA, orgSpec.isEnableNodeOUs());
        } catch (JavaChainException e) {
            System.err.println("Error generating MSP for org " + orgName);
            e.printStackTrace();
            System.exit(1);
        }
        generateNodes(nodeDir, orgSpec.getSpecs(), signCA, tlsCA, MspHelper.NODE, orgSpec.isEnableNodeOUs());

        List<NodeSpec> users = new ArrayList<>();
        int count = orgSpec.getUsers().getCount();
        for (int i = 1; i <= count; i++) {
            NodeSpec user = new NodeSpec();
            user.setCommonName(USER_BASE_NAME + i + "@" + orgName);
            users.add(user);
        }

        // add an admin user
        NodeSpec adminUser = new NodeSpec();
        adminUser.setCommonName(ADMIN_BASE_NAME + "@" + orgName);
        users.add(adminUser);

        generateNodes(userDir, users, signCA, tlsCA, MspHelper.CLIENT, orgSpec.isEnableNodeOUs());

        // copy the admin cert to the org's MSP admincerts
        try {
            copyAdminCert(userDir, adminCertDir, adminUser.getCommonName());
        } catch (JavaChainException e) {
            log.error("Error copying admin cert for org " + orgName, e);

            System.exit(1);
        }
        copyAllAdminCerts(userDir, nodeDir, orgName, orgSpec, adminUser);

        // copy the admin cert to each of the org's node's MSP admincerts
        for (NodeSpec spec : orgSpec.getSpecs()) {
            try {
                copyAdminCert(userDir, Paths.get(nodeDir, spec.getCommonName(), "msp", "admincerts").toString(), adminUser.getCommonName());
            } catch (JavaChainException e) {
                log.error("Error copying admin cert for org " + orgName + " node " + spec.getCommonName());
                log.error(e.getMessage());
                System.exit(1);
            }
        }
    }

    static void generateNodes(String baseDir, List<NodeSpec> nodes, CaHelper signCA, CaHelper tlsCA, int nodeType, boolean nodeOUs) {
        for (NodeSpec node : nodes) {
            String nodeDir = Paths.get(baseDir,node.getCommonName()).toString();
            File file = new File(nodeDir);

            if (!file.exists()) {
                try {
                    MspHelper.generateLocalMSP(nodeDir, node.getCommonName(), node.getSans(), signCA, tlsCA, nodeType, nodeOUs);
                } catch (JavaChainException e) {
                    log.error("Error generating local MSP for", e);
                    System.exit(1);
                }
            }
        }
    }


    static void generateConsenterOrgs(String baseDir, OrgSpec orgSpec) {
        String orgName = orgSpec.getDomain();
        System.out.println(orgName);
        // generate CAs
        String orgDir = Paths.get(baseDir, "consenterOrganizations", orgName).toString();
        String caDir = Paths.get(orgDir, "ca").toString();
        String tlsCADir = Paths.get(orgDir, "tlsca").toString();
        String mspDir = Paths.get(orgDir, "msp").toString();
        String consenterDir = Paths.get(orgDir, "consenters").toString();
        String userDir = Paths.get(orgDir, "users").toString();
        String adminCertDir = Paths.get(mspDir, "admincerts").toString();

        CaHelper signCA;
        CaHelper tlsCA;

        //generate signing CaHelper
        NodeSpec caSpec = orgSpec.getCa();

        signCA = generateCA(caDir, orgName, caSpec.getCommonName(), caSpec);

        //generate TLS CaHelper
        tlsCA = generateCA(tlsCADir, orgName, "tls" + caSpec.getCommonName(), caSpec);

        try {
            MspHelper.generateVerifyingMSP(mspDir, signCA, tlsCA, orgSpec.isEnableNodeOUs());
        } catch (JavaChainException e) {
            log.error("Error generating MSP for org " + orgName);
            log.error(e.getMessage());
            System.exit(1);
        }

        generateNodes(consenterDir, orgSpec.getSpecs(), signCA, tlsCA, MspHelper.CONSENTER, false);

        List<NodeSpec> users = new ArrayList<>();

        //add an admin user
        NodeSpec adminUser = new NodeSpec();
        adminUser.setCommonName(ADMIN_BASE_NAME + "@" + orgName);
        users.add(adminUser);

        generateNodes(userDir, users, signCA, tlsCA, MspHelper.CLIENT, false);

        //copy the admin cert to the org's MSP admincerts
        try {
            copyAdminCert(userDir, adminCertDir, adminUser.getCommonName());
        } catch (JavaChainException e) {
            log.error("Error copying admin cert for org " + orgName);
            log.error(e.getMessage());
            System.exit(1);
        }
        copyAllAdminCerts(userDir, consenterDir, orgName, orgSpec, adminUser);
    }


    private static CaHelper generateCA(String caDir, String orgName, String commonName, NodeSpec caSpec) {
        try {
            return CaHelper.newCA(caDir, orgName, commonName,
                    caSpec.getCountry(), caSpec.getProvince(), caSpec.getLocality(),
                    caSpec.getOrganizationUnit(), caSpec.getStreetAddress(),
                    caSpec.getPostalCode());
        } catch (JavaChainException e) {
            log.error("Error generating CaHelper for org " + orgName);
            log.error(e.getMessage());
            System.exit(1);
        }

        return null;
    }

    public static <T> T loadAs(String filePath, Class<T> tClass) throws JavaChainException {
        try {
            InputStream in;
            if (filePath == null) {
                in = new ByteArrayInputStream(ShowTemplateCmd.TEMPLATE.getBytes());
            } else {
                in = new FileInputStream(new File(filePath));
            }
            return new Yaml().loadAs(in, tClass);
        } catch (FileNotFoundException e) {
            throw new JavaChainException("file not found in file system while loading yaml, path: " + filePath);
        }
    }
}
