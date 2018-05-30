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
package org.bcia.javachain.consenter.common.msgprocessor;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.groupconfig.IGroupConfigBundle;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.common.multigroup.IStandardGroupSupport;
import org.bcia.javachain.consenter.entity.ConfigMsg;
import org.bcia.javachain.consenter.util.CommonUtils;
import org.bcia.javachain.consenter.util.TxUtils;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/5/18
 * @company Dingxuan
 */
public class StandardGroup implements IStandardGroupSupport, org.bcia.javachain.consenter.consensus.IProcessor {
    private static JavaChainLog log = JavaChainLogFactory.getLog(StandardGroup.class);
    private final static int MSGVERSION = 0;
    private final static int EPOCH = 0;
    IStandardGroupSupport support;

    IRule[] filters;

    public StandardGroup(IStandardGroupSupport support, IRule[] filters) {
        this.support = support;
        this.filters = filters;
    }

    public IRule[] createStandardChannelFilters(IGroupConfigBundle filterSupport) {
        //filterSupport.getGroupConfig();
        return null;
    }

    @Override
    public long sequence() {
        return 0;
    }

    @Override
    public String chainId() {
        return null;
    }

    @Override
    public ILocalSigner signer() {
        return null;
    }

    @Override
    public Configtx.ConfigEnvelope proposeConfigUpdate(Common.Envelope configtx) {
        return null;
    }

    @Override
    public boolean classfiyMsg(Common.GroupHeader chdr) {
        if (Common.HeaderType.CONFIG_VALUE == chdr.getType()) {
            return  true;
        }else if(Common.HeaderType.CONFIG_UPDATE_VALUE == chdr.getType()){
            return  true;
        }else if(Common.HeaderType.CONSENTER_TRANSACTION_VALUE == chdr.getType()){
            return  true;
        }else {
            return false;
        }
    }

    @Override
    public long processNormalMsg(Common.Envelope env) {
        long configSeq = support.sequence();
        return configSeq;
    }

    @Override
    public Object processConfigUpdateMsg(Common.Envelope env) throws InvalidProtocolBufferException, ValidateException, PolicyException {

        long seq = support.sequence();
        //TODO 通过apply过滤
        Configtx.ConfigEnvelope configEnvelope = support.proposeConfigUpdate(env);

        int headerType = 0;
        Common.Envelope config = TxUtils.createSignedEnvelope(headerType, support.chainId(), support.signer(), configEnvelope, MSGVERSION, EPOCH);

        return new ConfigMsg(config,seq);
    }

    @Override
    public Object processConfigMsg(Common.Envelope env) {
        Configtx.ConfigEnvelope configEnvelope = null;
        CommonUtils.unmarshalEnvelopeOfType(env, Common.HeaderType.CONFIG, configEnvelope);
        return null;
    }
}
