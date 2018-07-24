package org.bcia.julongchain.gossip.discovery;

import org.bcia.julongchain.protos.gossip.Message;

public class NetworkMember {

    private String endpoint;
    private byte[] metadata;
    private byte[] PKIid;
    private String internalEndpoint;
    private Message.Properties properties;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public byte[] getMetadata() {
        return metadata;
    }

    public void setMetadata(byte[] metadata) {
        this.metadata = metadata;
    }

    public byte[] getPKIid() {
        return PKIid;
    }

    public void setPKIid(byte[] PKIid) {
        this.PKIid = PKIid;
    }

    public String getInternalEndpoint() {
        return internalEndpoint;
    }

    public void setInternalEndpoint(String internalEndpoint) {
        this.internalEndpoint = internalEndpoint;
    }

    public Message.Properties getProperties() {
        return properties;
    }

    public void setProperties(Message.Properties properties) {
        this.properties = properties;
    }
}
