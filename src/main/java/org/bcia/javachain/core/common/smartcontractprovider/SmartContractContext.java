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
package org.bcia.javachain.core.common.smartcontractprovider;

import org.bcia.javachain.protos.node.ProposalPackage;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/15
 * @company Dingxuan
 */
public class SmartContractContext {

    private String chainID;
    private String name;
    private String version;
    private String txID;
    private Boolean sysSmartContract;
    private ProposalPackage.SignedProposal signedProposal;
    private ProposalPackage.Proposal proposal;
    private String canonicalName;

    public String getChainID() {
        return chainID;
    }

    public void setChainID(String chainID) {
        this.chainID = chainID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public Boolean getSysSmartContract() {
        return sysSmartContract;
    }

    public void setSysSmartContract(Boolean sysSmartContract) {
        this.sysSmartContract = sysSmartContract;
    }

    public ProposalPackage.SignedProposal getSignedProposal() {
        return signedProposal;
    }

    public void setSignedProposal(ProposalPackage.SignedProposal signedProposal) {
        this.signedProposal = signedProposal;
    }

    public ProposalPackage.Proposal getProposal() {
        return proposal;
    }

    public void setProposal(ProposalPackage.Proposal proposal) {
        this.proposal = proposal;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
}
