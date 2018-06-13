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
//package org.bcia.julongchain.consenter.common.msgprocessor;
//
//import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
//import org.bcia.julongchain.common.log.JavaChainLog;
//import org.bcia.julongchain.common.log.JavaChainLogFactory;
//import org.bcia.julongchain.consenter.consensus.IConsenterSupport;
//import org.bcia.julongchain.consenter.consensus.IReceiver;
//import org.bcia.julongchain.protos.common.Common;
//
///**
// * @author zhangmingyang
// * @Date: 2018/3/15
// * @company Dingxuan
// */
//public class MsgProcessor implements IConsenterSupport {
//    private static JavaChainLog log = JavaChainLogFactory.getLog(MsgProcessor.class);
//    @Override
//    public IReceiver blockCutter() {
//
//        return null;
//    }
//
//    @Override
//    public IGroupConfigBundle sharedConfig() {
//        return null;
//    }
//
//    @Override
//    public Common.Block createNextBlock(Common.Envelope[] messages) {
//        log.info("创建区块");
//        return null;
//    }
//
//    @Override
//    public void writeBlock(Common.Block block, byte[] encodedMetadataValue) {
//        log.info("区块写入");
//    }
//
//    @Override
//    public void writeConfigBlock(Common.Block block, byte[] encodedMetadataValue) {
//
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
