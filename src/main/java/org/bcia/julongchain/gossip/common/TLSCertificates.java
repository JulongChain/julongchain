package org.bcia.julongchain.gossip.common;

import org.bouncycastle.asn1.x509.Certificate;

public class TLSCertificates {

    private Certificate tlsServerCert;
    private Certificate tlsClientCert;

    public Certificate getTlsServerCert() {
        return tlsServerCert;
    }

    public void setTlsServerCert(Certificate tlsServerCert) {
        this.tlsServerCert = tlsServerCert;
    }

    public Certificate getTlsClientCert() {
        return tlsClientCert;
    }

    public void setTlsClientCert(Certificate tlsClientCert) {
        this.tlsClientCert = tlsClientCert;
    }
}
