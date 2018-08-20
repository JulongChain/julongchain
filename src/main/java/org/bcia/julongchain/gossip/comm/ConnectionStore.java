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
package org.bcia.julongchain.gossip.comm;

import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.gossip.gossip.ConnectionInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * class description
 *
 * @author wanliangbing
 * @date 18-7-24
 * @company Dingxuan
 */
public class ConnectionStore {

    private static final JulongChainLog log = JulongChainLogFactory.getLog(ConnectionInfo.class);
    private Boolean isClosing;
    private IConnFactory connFactory;
    private Map<String, Connection> pki2Conn;

    public static ConnectionStore newConnStore(IConnFactory connFactory) {
        ConnectionStore connectionStore = new ConnectionStore();
        connectionStore.setConnFactory(connFactory);
        connectionStore.setClosing(false);
        connectionStore.setPki2Conn(new HashMap<String, Connection>());
        return connectionStore;
    }

    public synchronized Connection getConnection(RemotePeer peer) throws GossipException {
        if (isClosing) {
            log.error("Shutting down");
            return null;
        }
        byte[] pkiID = peer.getPKIID();
        String endpoint = peer.getEndpoint();
        Connection connection = pki2Conn.get(new String(pkiID));
        if (connection != null) {
            return connection;
        }
        Connection createdConnection = connFactory.createConnection(endpoint, pkiID);
        if (isClosing) {
            log.error("ConnStore is closing");
            return null;
        }
        connection = pki2Conn.get(new String(pkiID));
        if (connection != null) {
            if (createdConnection != null) {
                createdConnection.close();;
            }
            return connection;
        }
        connection = createdConnection;
        pki2Conn.put(new String(createdConnection.getPkiID()), createdConnection);
        connection.serviceConnection();
        return connection;
    }

    public static JulongChainLog getLog() {
        return log;
    }

    public Boolean getClosing() {
        return isClosing;
    }

    public void setClosing(Boolean closing) {
        isClosing = closing;
    }

    public IConnFactory getConnFactory() {
        return connFactory;
    }

    public void setConnFactory(IConnFactory connFactory) {
        this.connFactory = connFactory;
    }

    public Map<String, Connection> getPki2Conn() {
        return pki2Conn;
    }

    public void setPki2Conn(Map<String, Connection> pki2Conn) {
        this.pki2Conn = pki2Conn;
    }
}
