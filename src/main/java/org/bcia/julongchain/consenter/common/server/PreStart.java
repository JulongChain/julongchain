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
package org.bcia.julongchain.consenter.common.server;


import org.bcia.julongchain.common.deliver.DeliverDeliverHandler;
import org.bcia.julongchain.common.deliver.DeliverSupport;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.genesis.GenesisBlockFactory;
import org.bcia.julongchain.common.ledger.blockledger.IFactory;
import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.julongchain.common.localmsp.ILocalSigner;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.common.util.proto.BlockUtils;
import org.bcia.julongchain.consenter.common.bootstrap.file.BootStrapHelper;
import org.bcia.julongchain.consenter.common.localconfig.ConsenterConfig;
import org.bcia.julongchain.consenter.common.multigroup.Registrar;
import org.bcia.julongchain.consenter.consensus.IConsensusPlugin;
import org.bcia.julongchain.consenter.consensus.singleton.Singleton;
import org.bcia.julongchain.consenter.util.ConsenterConstants;
import org.bcia.julongchain.core.common.grpc.GrpcServerConfig;
import org.bcia.julongchain.core.common.grpc.SecureOptions;
import org.bcia.julongchain.node.common.helper.ConfigTreeHelper;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfigFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 预启动
 *
 * @author zhangmingyang
 * @Date: 2018/5/29
 * @company Dingxuan
 */
public class PreStart {
    private static JulongChainLog log = JulongChainLogFactory.getLog(PreStart.class);
    private Registrar defaultRegistrar;


    public Registrar initializeMultichannelRegistrar(ConsenterConfig consenterConfig, ILocalSigner signer) throws JulongChainException, IOException {
        IFactory lf = LedgerHelper.createLedgerFactroy(consenterConfig);
        if (lf.groupIDs().size() == 0) {
            initBootstrapGroup(consenterConfig, lf);
        } else {
            log.info("Not bootstrapping because of existing chains");
        }
        Map<String, IConsensusPlugin> consenters = new HashMap<>();
        consenters.put("Singleton", new Singleton());
        Registrar registrar = new Registrar();
        defaultRegistrar = registrar.newRegistrar(lf, consenters, signer);
        return defaultRegistrar;
    }

    private static void initBootstrapGroup(ConsenterConfig consenterConfig, IFactory blockLedger) throws JulongChainException {
        Common.Block genesisBlock = null;
        switch (consenterConfig.getGeneral().getGenesisMethod()) {
            case "provisional":
                //根据配置生成创世区块
                GenesisConfig.Profile completedProfile = GenesisConfigFactory.getGenesisConfig().getCompletedProfile(consenterConfig.getGeneral().getGenesisProfile());
                Configtx.ConfigTree groupTree = ConfigTreeHelper.buildGroupTree(completedProfile);
                genesisBlock = new GenesisBlockFactory(groupTree).getGenesisBlock(consenterConfig.getFileLedger().getGroupName());
                break;
            case "file":
                genesisBlock = new BootStrapHelper(consenterConfig.getGeneral().getGenesisFile()).getGenesisBlock();
                break;
            default:
        }
        try {
            String chainId = BlockUtils.getGroupIDFromBlock(genesisBlock);
            ReadWriteBase gl = blockLedger.getOrCreate(chainId);
            gl.append(genesisBlock);
        } catch (JulongChainException e) {
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

    public Registrar getDefaultRegistrar() {
        return defaultRegistrar;
    }
}
