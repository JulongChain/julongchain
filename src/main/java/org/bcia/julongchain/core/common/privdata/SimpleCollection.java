/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.core.common.privdata;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policies.policy.IPolicy;
import org.bcia.julongchain.common.policycheck.policies.PolicyProvider;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.IIdentityDeserializer;
import org.bcia.julongchain.protos.common.Collection;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.common.Policies;

import java.util.ArrayList;
import java.util.List;

/**
 * SimpleCollection implements a collection with static properties
 * and a public member set
 *
 * @author sunzongyu
 * @date 2018/05/16
 * @company Dingxuan
 */
public class SimpleCollection implements ICollection, ICollectionAccessPolicy {

    private static final JavaChainLog log = JavaChainLogFactory.getLog(SimpleCollection.class);

    private String name;
    private IPolicy accessPolicy;
    private List<String> memberOrgs;
    private Collection.StaticCollectionConfig conf;

    /**
     *
     * Setup configures a simple collection object based on a given
     * StaticCollectionConfig proto that has all the necessary information
     * @param collectionConfig
     * @param deserializer
     */
    public void setUp(Collection.StaticCollectionConfig collectionConfig, IIdentityDeserializer deserializer) throws PolicyException {
        if(collectionConfig == null){
            throw new PolicyException("Null config passed to collection setup");
        }
        this.conf = collectionConfig;
        this.name = collectionConfig.getName();

        //获取通过的签名
        Collection.CollectionPolicyConfig collectionPolicyConfig = collectionConfig.getMemberOrgsPolicy();
        if(collectionPolicyConfig == null){
            throw new PolicyException("Collection config policy is null");
        }
        Policies.SignaturePolicyEnvelope accessPolicyEnvelope = collectionPolicyConfig.getSignaturePolicy();
        if(accessPolicyEnvelope == null){
            throw new PolicyException("Collection config access policy is null");
        }

        //在Envelope中获取accessPolicy
        PolicyProvider npp = new PolicyProvider(deserializer);
        byte[] polBytes = accessPolicyEnvelope.toByteArray();
        this.accessPolicy = npp.makePolicy(polBytes);

        //获取成员参数
        for(MspPrincipal.MSPPrincipal principal : accessPolicyEnvelope.getIdentitiesList()){
            switch (principal.getPrincipalClassification().getNumber()){
                case  MspPrincipal.MSPPrincipal.Classification.ROLE_VALUE:
                    MspPrincipal.MSPRole mspRole = null;
                    try {
                        mspRole = MspPrincipal.MSPRole.parseFrom(principal.getPrincipal());
                    } catch (InvalidProtocolBufferException e) {
                        log.error(e.getMessage(), e);
                        throw new PolicyException(e);
                    }
                    this.memberOrgs.add(mspRole.getMspIdentifier());
                    break;
                case  MspPrincipal.MSPPrincipal.Classification.IDENTITY_VALUE:
                    IIdentity principalId = null;
                    try {
                        principalId = deserializer.deserializeIdentity(principal.getPrincipal().toByteArray());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new PolicyException(e);
                    }
                    this.memberOrgs.add(principalId.getMSPIdentifier().getMspid());
                case  MspPrincipal.MSPPrincipal.Classification.ORGANIZATION_UNIT_VALUE:
                    MspPrincipal.OrganizationUnit ou = null;
                    try {
                        ou = MspPrincipal.OrganizationUnit.parseFrom(principal.getPrincipal());
                    } catch (InvalidProtocolBufferException e) {
                        log.error(e.getMessage(), e);
                        throw new PolicyException(e);
                    }
                    this.memberOrgs.add(ou.getMspIdentifier());
                default:
                    String errMsg = "Invalid principal type " + principal.getPrincipalClassificationValue();
                    log.error(errMsg);
                    throw new PolicyException(errMsg);
            }
        }
    }

    /**
     * CollectionID returns the collection's ID
     * @return
     */
    @Override
    public String collectionID() {
        return this.name;
    }

    /**
     * MemberOrgs returns the MSP IDs that are part of this collection
     * @return
     */
    @Override
    public List<String> memberOrgs() {
        return this.memberOrgs;
    }

    /**
     * AccessFilter returns the member filter function that evaluates signed data
     * against the member access policy of this collection
     * @param sd
     * @return
     */
    @Override
    public boolean getAccessFilter(SignedData sd) {
        List<SignedData> l = new ArrayList<>();
        l.add(sd);
        try {
            this.accessPolicy.evaluate(l);
        } catch (PolicyException e) {
            return false;
        }
        return true;
    }

    /**
     * AccessFilter returns the member filter function that evaluates signed data
     * against the member access policy of this collection
     * @return
     */
    @Override
    public int getRequiredNodeCount() {
        return this.conf.getRequiredPeerCount();
    }

    @Override
    public int getMaximumNodeCount() {
        return this.conf.getMaximumPeerCount();
    }

    public void setName(String name) {
        this.name = name;
    }

    public IPolicy getAccessPolicy() {
        return accessPolicy;
    }

    public void setAccessPolicy(IPolicy accessPolicy) {
        this.accessPolicy = accessPolicy;
    }

    public void setMemberOrgs(List<String> memberOrgs) {
        this.memberOrgs = memberOrgs;
    }

    public Collection.StaticCollectionConfig getConf() {
        return conf;
    }

    public void setConf(Collection.StaticCollectionConfig conf) {
        this.conf = conf;
    }
}
