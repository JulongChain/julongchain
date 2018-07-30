/**
 * Copyright Dingxuan. All Rights Reserved.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.container.ccintf;

import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.util.BytesHexStrTranslate;
import org.bcia.julongchain.csp.gm.sdt.sm3.SM3;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/2
 * @company Dingxuan
 */
public class SCID {

  private SmartContractPackage.SmartContractSpec smartContractSpec;
  private String networkID;
  private String nodeID;
  private String chainID;
  private String version;

  public SmartContractPackage.SmartContractSpec getSmartContractSpec() {
    return smartContractSpec;
  }

  public void setSmartContractSpec(SmartContractPackage.SmartContractSpec smartContractSpec) {
    this.smartContractSpec = smartContractSpec;
  }

  public String getNetworkID() {
    return networkID;
  }

  public void setNetworkID(String networkID) {
    this.networkID = networkID;
  }

  public String getNodeID() {
    return nodeID;
  }

  public void setNodeID(String nodeID) {
    this.nodeID = nodeID;
  }

  public String getChainID() {
    return chainID;
  }

  public void setChainID(String chainID) {
    this.chainID = chainID;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getName() throws JavaChainException {
    if (this.smartContractSpec == null) {
      throw new JavaChainException("nil smart contract spec");
    }

    String name = this.smartContractSpec.getSmartContractId().getName();
    if (StringUtils.isNotEmpty(this.version)) {
      name = name + "-" + this.version;
    }

    if (StringUtils.isNotEmpty(this.chainID)) {
      byte[] hash = new SM3().hash(this.chainID.getBytes());
      String hexStr = BytesHexStrTranslate.bytesToHexFun1(hash);
      name = name + "-" + hexStr;
    }

    return name;
  }
}
