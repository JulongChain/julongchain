///**
// * Copyright DingXuan. All Rights Reserved.
// * <p>
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// * http://www.apache.org/licenses/LICENSE-2.0
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.bcia.julongchain.consenter.common.multigroup;
//
//import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
//import org.bcia.julongchain.common.log.JavaChainLog;
//import org.bcia.julongchain.common.log.JavaChainLogFactory;
//import org.bcia.julongchain.consenter.consensus.IConsenterSupport;
//import org.bcia.julongchain.consenter.consensus.IReceiver;
//import org.bcia.julongchain.protos.common.Common;
//import org.springframework.stereotype.Component;
//
///**
// * solo和kafka创建区块的公共部分
// * @author zhangmingyang
// * @Date: 2018/3/19
// * @company Dingxuan
// */
//@Component
//public class MultiGroup implements IConsenterSupport {
//    private static JavaChainLog log = JavaChainLogFactory.getLog(MultiGroup.class);
//    @Override
//    public IReceiver blockCutter() {
//        return null;
//    }
//
//    @Override
//    public IGroupConfigBundle sharedConfig() {
//        return null;
//    }
//
//
//    @Override
//    public Common.Block createNextBlock(Common.Envelope[] messages) {
//        log.info("进入创建下一个区块的方法体");
//
//        return null;
//    }
//
//    @Override
//    public void writeBlock(Common.Block block, byte[] encodedMetadataValue) {
//
//    }
//
//    @Override
//    public void writeConfigBlock(Common.Block block, byte[] encodedMetadataValue) {
//      log.info("写配置区块的方法");
//    }
//
//    @Override
//    public long sequence() {
//        return 0;
//    }
//
//    @Override
//    public String chainID() {
//        return null;
//    }
//
//    @Override
//    public long height() {
//        return 0;
//    }
//
//    @Override
//    public Common.SignatureHeader newSignatureHeader() {
//        return null;
//    }
//
//    @Override
//    public byte[] sign(byte[] message) {
//        return new byte[0];
//    }
//
//    @Override
//    public boolean classfiyMsg(Common.GroupHeader chdr) {
//        return false;
//    }
//
//    @Override
//    public long processNormalMsg(Common.Envelope env) {
//        return 0;
//    }
//
//    @Override
//    public Object processConfigUpdateMsg(Common.Envelope env) {
//        return null;
//    }
//
//    @Override
//    public Object processConfigMsg(Common.Envelope env) {
//        return null;
//    }
//}
