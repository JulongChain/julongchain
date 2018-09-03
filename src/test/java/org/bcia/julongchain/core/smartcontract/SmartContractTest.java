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
package org.bcia.julongchain.core.smartcontract;

import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import org.apache.commons.io.FileUtils;
import org.bcia.julongchain.common.exception.SysSmartContractException;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.core.node.NodeConfigFactory;
import org.bcia.julongchain.core.smartcontract.shim.impl.MockStub;
import org.bcia.julongchain.core.ssc.lssc.LSSC;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 类描述
 *
 * @author
 * @date 2018/8/27
 * @company Dingxuan
 */
public class SmartContractTest {

    @Test
    public void testInstall() throws SysSmartContractException {

        String scName = "mycc";
        String version = "1.0";
        String srcPath = "/root/install";
        String installCmd = "install";

        LSSC lssc = new LSSC();
        MockStub stub = new MockStub(CommConstant.LSSC, lssc);
        lssc.init(stub);

        SmartContractPackage.SmartContractID smartContractID = SmartContractPackage.SmartContractID.newBuilder().setName(scName).setVersion(version).setPath(srcPath).build();
        SmartContractPackage.SmartContractSpec smartContractSpec = SmartContractPackage.SmartContractSpec.newBuilder().setSmartContractId(smartContractID).build();
        SmartContractPackage.SmartContractDeploymentSpec smartContractDeploymentSpec = SmartContractPackage.SmartContractDeploymentSpec.newBuilder().setSmartContractSpec(smartContractSpec).build();

        List<ByteString> list = new ArrayList<ByteString>();
        list.add(ByteString.copyFrom(installCmd.getBytes()));
        list.add(smartContractDeploymentSpec.toByteString());

        lssc.executeInstall(stub, list.get(1).toByteArray());

        String path = NodeConfigFactory.getNodeConfig().getNode().getFileSystemPath();
        File scFile = FileUtils.getFile(path, scName + "." + version);

        Assert.assertTrue(scFile.exists());
    }

    @Test
    public void testInstantiate() {

    }

    @Test
    public void testInvoke() {

    }

}
