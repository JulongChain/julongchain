package org.bcia.julongchain.gossip.gossip;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.protos.gossip.Message;

public class SignedGossipMessage {

    private Message.Envelope envelope;
    private Message.GossipMessage gossipMessage;

    public Message.Envelope sign(ISigner signer) throws GossipException {
        Message.SecretEnvelope secretEnvelope = null;
        if (this.getEnvelope() != null) {
            secretEnvelope = this.getEnvelope().getSecretEnvelope();
        }
        this.setEnvelope(null);
        byte[] payload = this.getGossipMessage().toByteArray();
        byte[] sig = signer.execute(payload);
        Message.Envelope newEnvelope = Message.Envelope.newBuilder()
                .setPayload(ByteString.copyFrom(payload))
                .setSignature(ByteString.copyFrom(sig))
                .setSecretEnvelope(secretEnvelope)
                .build();
        this.setEnvelope(newEnvelope);
        return newEnvelope;
    }

    public void verify(byte[] peerIdentity, IVerifier verify) throws GossipException {
        if (this.getEnvelope() == null) {
            throw new GossipException("Missing envelope");
        }
        if (this.getEnvelope().getPayload().size() == 0) {
            throw new GossipException("Empty payload");
        }
        if (this.getEnvelope().getSignature().size() == 0) {
            throw new GossipException("Empty signature");
        }
        verify.execute(peerIdentity, this.getEnvelope().getSignature().toByteArray(), this.getEnvelope().getPayload().toByteArray());
        if (this.getEnvelope().getSecretEnvelope() != null) {
            byte[] payload = this.getEnvelope().getSecretEnvelope().getPayload().toByteArray();
            byte[] sig = this.getEnvelope().getSecretEnvelope().getSignature().toByteArray();
            if (payload.length == 0) {
                throw new GossipException("Empty payload");
            }
            if (sig.length == 0) {
                throw new GossipException("Empty signature");
            }
            verify.execute(peerIdentity, sig, payload);
        }
    }

    public Boolean isSigned() {
        return this.getEnvelope() != null
                && this.getEnvelope().getPayload() != null
                && this.getEnvelope().getSignature() != null;
    }

    public Message.Envelope getEnvelope() {
        return envelope;
    }

    public void setEnvelope(Message.Envelope envelope) {
        this.envelope = envelope;
    }

    public Message.GossipMessage getGossipMessage() {
        return gossipMessage;
    }

    public void setGossipMessage(Message.GossipMessage gossipMessage) {
        this.gossipMessage = gossipMessage;
    }
}
