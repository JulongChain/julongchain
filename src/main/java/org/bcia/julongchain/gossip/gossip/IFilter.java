package org.bcia.julongchain.gossip.gossip;

public abstract class IFilter {

    private byte[] bytes1 = new byte[]{};

    public abstract Boolean filter(byte[] bytes2);

    public byte[] getBytes1() {
        return bytes1;
    }

    public void setBytes1(byte[] bytes1) {
        this.bytes1 = bytes1;
    }
}
