/**
 * Copyright Aisino. All Rights Reserved.
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

package org.bcia.julongchain.common.policycheck;

import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.policies.config.SignaturePolicy;
import org.bcia.julongchain.common.policycheck.cauthdsl.CAuthDsl;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.msp.mgmt.Msp;
import org.bcia.julongchain.msp.mgmt.MspManager;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.common.Policies;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 11/05/18
 * @company Aisino
 */
public class CAuthDslTest {
    @Before
    public void setUp() {
        System.out.println("setup...");
    }

    @After
    public void finalize(){
        System.out.println("finalize...");
    }

    @Test
    public void deduplicate() throws PolicyException {
        List<SignedData> sds = new ArrayList<SignedData>();
        SignedData sd1 = new SignedData(null,"id1111111111111111".getBytes(),null);
        SignedData sd2 = new SignedData(null,"id2222222222222222".getBytes(),null);
        SignedData sd3 = new SignedData(null,"id3333333333333333".getBytes(),null);
        sds.add(sd1);
        sds.add(sd2);
        sds.add(sd3);
       // MspManager deserializer = new MspManager();//mock(MspManager.class);
        Msp deserializer = new Msp();
        MspManager m = new MspManager();
        CAuthDsl.deduplicate(sds,deserializer);
    }
    @Test
    public void compile() throws PolicyException {
        List<SignedData> sds = new ArrayList<SignedData>();
        SignedData sd1 = new SignedData("A".getBytes(),"id111111111".getBytes(),"A".getBytes());
        SignedData sd2 = new SignedData("B".getBytes(),"id222222222".getBytes(),"B".getBytes());
        SignedData sd3 = new SignedData("C".getBytes(),"id333333333".getBytes(),"C".getBytes());
        sds.add(sd1);
        sds.add(sd2);
        sds.add(sd3);
        MspManager deserializer = new MspManager();//mock(MspManager.class);
        Policies.SignaturePolicy policy = Policies.SignaturePolicy.newBuilder().build();
        List<MspPrincipal.MSPPrincipal> identities = new ArrayList<MspPrincipal.MSPPrincipal>();
        Msp deserializers = new Msp();
        CAuthDsl.compile(policy,identities,deserializers);


    }
}
