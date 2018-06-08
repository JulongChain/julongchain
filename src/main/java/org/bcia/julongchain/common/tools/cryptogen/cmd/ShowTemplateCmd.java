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

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.consenter.util.LoadYaml;

import java.io.IOException;
import java.net.URL;

/**
 * @author chenhao, liuxifeng
 * @date 2018/4/4
 * @company Excelsecu
 */
public class ShowTemplateCmd implements ICryptoGenCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ShowTemplateCmd.class);

    private static final String DEFAULT_TEMPLATE =
            "# ---------------------------------------------------------------------------\n" +
            "# \"ConsenterOrgs\" - Definition of organizations managing consenter nodes\n" +
            "# ---------------------------------------------------------------------------\n" +
            "!!org.bcia.julongchain.common.tools.cryptogen.bean.Config\n" +
            "consenterOrgs:\n" +
            "  # ---------------------------------------------------------------------------\n" +
            "  # Consenter\n" +
            "  # ---------------------------------------------------------------------------\n" +
            "  - name: Consenter\n" +
            "    domain: example.com\n" +
            "\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    # \"Specs\" - See PeerOrgs below for complete description\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    specs:\n" +
            "      - hostname: consenter\n" +
            "\n" +
            "# ---------------------------------------------------------------------------\n" +
            "# \"PeerOrgs\" - Definition of organizations managing peer nodes\n" +
            "# ---------------------------------------------------------------------------\n" +
            "peerOrgs:\n" +
            "  # ---------------------------------------------------------------------------\n" +
            "  # Org1\n" +
            "  # ---------------------------------------------------------------------------\n" +
            "  - name: Org1\n" +
            "    domain: org1.example.com\n" +
            "    enableNodeOUs: false\n" +
            "\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    # \"CA\"\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    # Uncomment this section to enable the explicit definition of the CA for this\n" +
            "    # organization.  This entry is a Spec.  See \"Specs\" section below for details.\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    # CA:\n" +
            "    #    Hostname: ca # implicitly ca.org1.example.com\n" +
            "    #    Country: US\n" +
            "    #    Province: California\n" +
            "    #    Locality: San Francisco\n" +
            "    #    OrganizationalUnit: Hyperledger Fabric\n" +
            "    #    StreetAddress: address for org # default nil\n" +
            "    #    PostalCode: postalCode for org # default nil\n" +
            "\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    # \"Specs\"\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    # Uncomment this section to enable the explicit definition of hosts in your\n" +
            "    # configuration.  Most users will want to use Template, below\n" +
            "    #\n" +
            "    # Specs is an array of Spec entries.  Each Spec entry consists of two fields:\n" +
            "    #   - Hostname:   (Required) The desired hostname, sans the domain.\n" +
            "    #   - CommonName: (Optional) Specifies the template or explicit override for\n" +
            "    #                 the CN.  By default, this is the template:\n" +
            "    #\n" +
            "    #                              \"{{.Hostname}}.{{.Domain}}\"\n" +
            "    #\n" +
            "    #                 which obtains its values from the Spec.Hostname and\n" +
            "    #                 Org.Domain, respectively.\n" +
            "    #   - SANS:       (Optional) Specifies one or more Subject Alternative Names\n" +
            "    #                 to be set in the resulting x509. Accepts template\n" +
            "    #                 variables {{.Hostname}}, {{.Domain}}, {{.CommonName}}. IP\n" +
            "    #                 addresses provided here will be properly recognized. Other\n" +
            "    #                 values will be taken as DNS names.\n" +
            "    #                 NOTE: Two implicit entries are created for you:\n" +
            "    #                     - {{ .CommonName }}\n" +
            "    #                     - {{ .Hostname }}\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    # Specs:\n" +
            "    #   - Hostname: foo # implicitly \"foo.org1.example.com\"\n" +
            "    #     CommonName: foo27.org5.example.com # overrides Hostname-based FQDN set above\n" +
            "    #     SANS:\n" +
            "    #       - \"bar.{{.Domain}}\"\n" +
            "    #       - \"altfoo.{{.Domain}}\"\n" +
            "    #       - \"{{.Hostname}}.org6.net\"\n" +
            "    #       - 172.16.10.31\n" +
            "    #   - Hostname: bar\n" +
            "    #   - Hostname: baz\n" +
            "\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    # \"Template\"\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    # Allows for the definition of 1 or more hosts that are created sequentially\n" +
            "    # from a template. By default, this looks like \"peer%d\" from 0 to Count-1.\n" +
            "    # You may override the number of nodes (Count), the starting index (Start)\n" +
            "    # or the template used to construct the name (Hostname).\n" +
            "    #\n" +
            "    # Note: Template and Specs are not mutually exclusive.  You may define both\n" +
            "    # sections and the aggregate nodes will be created for you.  Take care with\n" +
            "    # name collisions\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    template:\n" +
            "      count: 1\n" +
            "      # Start: 5\n" +
            "      # Hostname: {{.Prefix}}{{.Index}} # default\n" +
            "      # SANS:\n" +
            "      #   - \"{{.Hostname}}.alt.{{.Domain}}\"\n" +
            "\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    # \"Users\"\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    # Count: The number of user accounts _in addition_ to Admin\n" +
            "    # ---------------------------------------------------------------------------\n" +
            "    users:\n" +
            "      count: 1\n" +
            "\n" +
            "  # ---------------------------------------------------------------------------\n" +
            "  # Org2: See \"Org1\" for full specification\n" +
            "  # ---------------------------------------------------------------------------\n" +
            "  - name: Org2\n" +
            "    domain: org2.example.com\n" +
            "    enableNodeOUs: false\n" +
            "    template:\n" +
            "      count: 1\n" +
            "    users:\n" +
            "      count: 1";

    static final String template;

    @Override
    public void execCmd(String[] args) {
        System.out.println(template);
    }

    static {
        String temp = DEFAULT_TEMPLATE;
        URL url = LoadYaml.class.getClassLoader().getResource("crypto-config.yaml");
        if (url == null) {
            log.warn("crypto-config.yaml not found in jar, use default template");
        } else {
            try {
                temp = new String(FileUtils.readFileBytes(url.getPath()));
            } catch (IOException e) {
                log.warn("read crypto-config.yaml in jar failed, use default template");
                temp = DEFAULT_TEMPLATE;
            }
        }
        template = temp;
    }
}
