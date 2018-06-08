/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.javachain.consenter.common.server;


import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.common.ledger.blockledger.IFactory;
import org.bcia.javachain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.localmsp.impl.LocalSigner;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.tools.cryptogen.FileUtil;
import org.bcia.javachain.common.util.FileUtils;
import org.bcia.javachain.common.util.proto.BlockUtils;
import org.bcia.javachain.consenter.common.bootstrap.file.BootStrapHelper;
import org.bcia.javachain.consenter.common.localconfig.ConsenterConfig;
import org.bcia.javachain.consenter.common.multigroup.Registrar;
import org.bcia.javachain.consenter.consensus.IConsensue;
import org.bcia.javachain.consenter.consensus.singleton.Singleton;
import org.bcia.javachain.core.common.grpc.GrpcServerConfig;
import org.bcia.javachain.core.common.grpc.SecureOptions;
import org.bcia.javachain.msp.mgmt.GlobalMspManagement;
import org.bcia.javachain.node.common.helper.ConfigTreeHelper;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfigFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.bcia.javachain.consenter.common.localconfig.ConsenterConfigFactory.loadConsenterConfig;

/**
 * @author zhangmingyang
 * @Date: 2018/5/29
 * @company Dingxuan
 */
public class PreStart {
    private static JavaChainLog log = JavaChainLogFactory.getLog(PreStart.class);

    public static Registrar initializeMultichannelRegistrar(ConsenterConfig consenterConfig, ILocalSigner signer) throws JavaChainException, IOException {
        IFactory lf = LedgerHelper.createLedgerFactroy(consenterConfig);
        if (lf.groupIDs().size() == 0) {
            initBootstrapGroup(consenterConfig, lf);
        } else {
            log.info("Not bootstrapping because of existing chains");
        }
        Map<String, IConsensue> consenters = new HashMap<>();
        consenters.put("Singleton", new Singleton());
        return new Registrar().newRegistrar(lf, consenters, signer);
    }

    private static void initBootstrapGroup(ConsenterConfig consenterConfig, IFactory blockLedger) throws IOException, JavaChainException {
        Common.Block genesisBlock = null;
        switch (consenterConfig.getGeneral().getGenesisMethod()) {
            case "provisional":
                //TODO 临时文件生成创世区块
                GenesisConfig.Profile completedProfile = GenesisConfigFactory.loadGenesisConfig().getCompletedProfile(consenterConfig.getGeneral().getGenesisProfile());
                Configtx.ConfigTree groupTree = ConfigTreeHelper.buildGroupTree(completedProfile);
                genesisBlock = new GenesisBlockFactory(groupTree).getGenesisBlock("systemGroup");
                break;
            case "file":
                genesisBlock = new BootStrapHelper(consenterConfig.getGeneral().getGenesisFile()).genesisBlock();
                break;
            default:
        }
        try {
            String chainId = BlockUtils.getGroupIDFromBlock(genesisBlock);
            ReadWriteBase gl = blockLedger.getOrCreate(chainId);
            gl.append(genesisBlock);
        } catch (JavaChainException e) {
            e.printStackTrace();
            e.printStackTrace();
        }
    }

    private static GrpcServerConfig initializeServerConfig(ConsenterConfig consenterConfig) {
        SecureOptions secureOptions = new SecureOptions();
        secureOptions.setUseTLS(Boolean.valueOf(consenterConfig.getGeneral().getTls().get("enabled")));
        secureOptions.setRequireClientCert(Boolean.valueOf(consenterConfig.getGeneral().getTls().get("clientAuthRequired")));
        if (secureOptions.isUseTLS()) {
            String msg = "TLS";
            byte[] serverCert;
            byte[] serverKey;
            try {
                serverCert = FileUtils.readFileBytes(consenterConfig.getGeneral().getTls().get("certificate"));
            } catch (IOException e) {
                log.error(String.format("Failed to load server Certificate file '%s' (%s)",
                        consenterConfig.getGeneral().getTls().get("certificate"), e.getMessage()));
            }
            try {
                serverKey = FileUtils.readFileBytes(consenterConfig.getGeneral().getTls().get("privateKey"));
            } catch (IOException e) {
                log.error(String.format("Failed to load PrivateKey file '%s' (%s)",
                        consenterConfig.getGeneral().getTls().get("privateKey"), e.getMessage()));
            }
            //TODO clientRootClient String[]
        }
        return new GrpcServerConfig();
    }

    public static void main(String[] args) throws IOException, JavaChainException {
        LocalSigner localSigner = new LocalSigner();

        Registrar registrar = initializeMultichannelRegistrar(loadConsenterConfig(), localSigner);

    }
}
