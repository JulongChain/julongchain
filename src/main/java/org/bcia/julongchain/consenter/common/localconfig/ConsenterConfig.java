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
package org.bcia.julongchain.consenter.common.localconfig;

import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/5/24
 * @company Dingxuan
 */
public class ConsenterConfig {
    public final static String CONSENTER_CONFIG_PATH= "consenter.yaml";
    private General general;

    private FileLedger fileLedger;

    private RamLedger ramLedger;

    private Kafka kafka;

    private Debug debug;


    public static class General {
        private String ledgerType;
        private String gossipAddress;
        private String listenAddress;
        private String listenPort;
        private Map<String,String> tls;
        private Map<String,Integer> keepAlive;
        private String logLevel;
        private String logFormat;
        private String genesisMethod;
        private String genesisProfile;
        private String genesisFile;
        private String localMspDir;
        private String localMspId;
        private Map<String,String> profile;
        private Bccsp bccsp;
        private Map<String,Integer> authentication;

        public String getLedgerType() {
            return ledgerType;
        }

        public void setLedgerType(String ledgerType) {
            this.ledgerType = ledgerType;
        }

        public String getListenAddress() {
            return listenAddress;
        }

        public void setListenAddress(String listenAddress) {
            this.listenAddress = listenAddress;
        }

        public String getListenPort() {
            return listenPort;
        }

        public void setListenPort(String listenPort) {
            this.listenPort = listenPort;
        }

        public Map<String, String> getTls() {
            return tls;
        }

        public void setTls(Map<String, String> tls) {
            this.tls = tls;
        }

        public Map<String, Integer> getKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(Map<String, Integer> keepAlive) {
            this.keepAlive = keepAlive;
        }

        public String getLogLevel() {
            return logLevel;
        }

        public void setLogLevel(String logLevel) {
            this.logLevel = logLevel;
        }

        public String getLogFormat() {
            return logFormat;
        }

        public void setLogFormat(String logFormat) {
            this.logFormat = logFormat;
        }

        public String getGenesisMethod() {
            return genesisMethod;
        }

        public void setGenesisMethod(String genesisMethod) {
            this.genesisMethod = genesisMethod;
        }

        public String getGenesisProfile() {
            return genesisProfile;
        }

        public void setGenesisProfile(String genesisProfile) {
            this.genesisProfile = genesisProfile;
        }

        public String getGenesisFile() {
            return genesisFile;
        }

        public void setGenesisFile(String genesisFile) {
            this.genesisFile = genesisFile;
        }

        public String getLocalMspDir() {
            return localMspDir;
        }

        public void setLocalMspDir(String localMspDir) {
            this.localMspDir = localMspDir;
        }

        public String getLocalMspId() {
            return localMspId;
        }

        public void setLocalMspId(String localMspId) {
            this.localMspId = localMspId;
        }

        public Map<String, String> getProfile() {
            return profile;
        }

        public void setProfile(Map<String, String> profile) {
            this.profile = profile;
        }

        public Bccsp getBccsp() {
            return bccsp;
        }

        public void setBccsp(Bccsp bccsp) {
            this.bccsp = bccsp;
        }

        public Map<String, Integer> getAuthentication() {
            return authentication;
        }

        public void setAuthentication(Map<String, Integer> authentication) {
            this.authentication = authentication;
        }

        public String getGossipAddress() {
            return gossipAddress;
        }

        public void setGossipAddress(String gossipAddress) {
            this.gossipAddress = gossipAddress;
        }
    }

    public static class FileLedger {
        private String location;
        private String prefix;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public static class RamLedger {
        private int historySize;

        public int getHistorySize() {
            return historySize;
        }

        public void setHistorySize(int historySize) {
            this.historySize = historySize;
        }
    }


    public static class Kafka {
        private Map<String,String> comumer;
        private Map<String,String> server;
        private Map<String,String> zookeeper;
        private Retry retry;
        private Map<String,String> tls;
        private boolean verbose;
        private String version;

        public Map<String, String> getComumer() {
            return comumer;
        }

        public void setComumer(Map<String, String> comumer) {
            this.comumer = comumer;
        }

        public Map<String, String> getServer() {
            return server;
        }

        public void setServer(Map<String, String> server) {
            this.server = server;
        }

        public Map<String, String> getZookeeper() {
            return zookeeper;
        }

        public void setZookeeper(Map<String, String> zookeeper) {
            this.zookeeper = zookeeper;
        }

        public Retry getRetry() {
            return retry;
        }

        public void setRetry(Retry retry) {
            this.retry = retry;
        }

        public Map<String, String> getTls() {
            return tls;
        }

        public void setTls(Map<String, String> tls) {
            this.tls = tls;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public boolean isVerbose() {
            return verbose;
        }

        public void setVerbose(boolean verbose) {
            this.verbose = verbose;
        }
    }

    public static class Debug {
        private String broadcastTraceDir;
        private String deliverTraceDir;

        public String getDeliverTraceDir() {
            return deliverTraceDir;
        }

        public void setDeliverTraceDir(String deliverTraceDir) {
            this.deliverTraceDir = deliverTraceDir;
        }

        public String getBroadcastTraceDir() {
            return broadcastTraceDir;
        }

        public void setBroadcastTraceDir(String broadcastTraceDir) {
            this.broadcastTraceDir = broadcastTraceDir;
        }
    }

    public static class Bccsp{
        private String defaultValue;
        private Map<String,String> sw;

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Map<String, String> getSw() {
            return sw;
        }

        public void setSw(Map<String, String> sw) {
            this.sw = sw;
        }
    }

    public static class Retry{
        private int shortInterval;
        private int shortTotal;
        private int longInterval;
        private int longTotal;
        private Map<String,Integer> networkTimeouts;
        private Map<String,Integer> metadata;
        private Map<String,Integer> producer;
        private Map<String,Integer> consumer;


        public int getLongTotal() {
            return longTotal;
        }

        public void setLongTotal(int longTotal) {
            this.longTotal = longTotal;
        }

        public int getShortInterval() {
            return shortInterval;
        }

        public void setShortInterval(int shortInterval) {
            this.shortInterval = shortInterval;
        }

        public int getShortTotal() {
            return shortTotal;
        }

        public void setShortTotal(int shortTotal) {
            this.shortTotal = shortTotal;
        }

        public int getLongInterval() {
            return longInterval;
        }

        public void setLongInterval(int longInterval) {
            this.longInterval = longInterval;
        }

        public Map<String, Integer> getNetworkTimeouts() {
            return networkTimeouts;
        }

        public void setNetworkTimeouts(Map<String, Integer> networkTimeouts) {
            this.networkTimeouts = networkTimeouts;
        }

        public Map<String, Integer> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Integer> metadata) {
            this.metadata = metadata;
        }

        public Map<String, Integer> getProducer() {
            return producer;
        }

        public void setProducer(Map<String, Integer> producer) {
            this.producer = producer;
        }

        public Map<String, Integer> getConsumer() {
            return consumer;
        }

        public void setConsumer(Map<String, Integer> consumer) {
            this.consumer = consumer;
        }
    }

    public General getGeneral() {
        return general;
    }

    public void setGeneral(General general) {
        this.general = general;
    }

    public FileLedger getFileLedger() {
        return fileLedger;
    }

    public void setFileLedger(FileLedger fileLedger) {
        this.fileLedger = fileLedger;
    }

    public RamLedger getRamLedger() {
        return ramLedger;
    }

    public void setRamLedger(RamLedger ramLedger) {
        this.ramLedger = ramLedger;
    }

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
    }

    public Debug getDebug() {
        return debug;
    }

    public void setDebug(Debug debug) {
        this.debug = debug;
    }
}
