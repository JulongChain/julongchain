/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified by Dingxuan sunianle on 2018-03-01
*/
package org.bcia.javachain.core.smartcontract.shim.intfs;

import io.grpc.ManagedChannel;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.apache.commons.cli.Options;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.bcia.javachain.core.smartcontract.shim.impl.ChatStream;
import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.protos.node.Smartcontract.SmartContractID;
import org.bcia.javachain.protos.node.SmartcontractShim.SmartContractMessage;
import org.bcia.javachain.protos.node.SmartcontractShim.SmartContractMessage.Type;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.bcia.javachain.core.smartcontract.shim.impl.Response.Status.INTERNAL_SERVER_ERROR;
import static org.bcia.javachain.core.smartcontract.shim.impl.Response.Status.SUCCESS;

/**
 * 用于创建智能合约的抽象基类
 *
 * @author sunianle
 * @date 3/1/18
 * @company Dingxuan
 */
public abstract class SmartContractBase implements ISmartContract {
    @Override
    public abstract Response init(ISmartContractStub stub);

    @Override
    public abstract Response invoke(ISmartContractStub stub);

    @Override
    public abstract String getSmartContractDescription();

    private static JavaChainLog log = JavaChainLogFactory.getLog(SmartContractBase.class);

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 7051;

    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    private String hostOverrideAuthority = "";
    private boolean tlsEnabled = false;
    private String rootCertFile = "/etc/hyperledger/fabric/peer.crt";

    private String id;

    private final static String CORE_SMARTCONTRACT_ID_NAME = "CORE_SMARTCONTRACT_ID_NAME";
    private final static String CORE_NODE_ADDRESS = "CORE_NODE_ADDRESS";
    private final static String CORE_NODE_TLS_ENABLED = "CORE_NODE_TLS_ENABLED";
    private final static String CORE_NODE_TLS_SERVERHOSTOVERRIDE = "CORE_NODE_TLS_SERVERHOSTOVERRIDE";
    private static final String CORE_NODE_TLS_ROOTCERT_FILE = "CORE_NODE_TLS_ROOTCERT_FILE";

    /**
     * Start chaincode
     *
     * @param args command line arguments
     */
    public void start(String[] args) {
        processEnvironmentOptions();
        processCommandLineOptions(args);
        if (this.id == null) {
            log.error(String.format("The chaincode id must be specified using either the -i or --i command line options or the %s environment variable.", CORE_SMARTCONTRACT_ID_NAME));
        }
        new Thread(() -> {
            log.trace("chaincode started");
            final ManagedChannel connection = newPeerClientConnection();
            log.trace("connection created");
            chatWithPeer(connection);
            log.trace("chatWithPeer DONE");
        }).start();
    }

    private void processCommandLineOptions(String[] args) {
        Options options = new Options();
        options.addOption("a", "peerAddress", true, "Address of peer to connect to");
        options.addOption("s", "securityEnabled", false, "Present if security is enabled");
        options.addOption("i", "id", true, "Identity of chaincode");
        options.addOption("o", "hostNameOverride", true, "Hostname override for server certificate");
        try {
            CommandLine cl = new DefaultParser().parse(options, args);
            if (cl.hasOption('a')) {
                host = cl.getOptionValue('a');
                port = new Integer(host.split(":")[1]);
                host = host.split(":")[0];
            }
            if (cl.hasOption('s')) {
                tlsEnabled = true;
                log.info("TLS enabled");
                if (cl.hasOption('o')) {
                    hostOverrideAuthority = cl.getOptionValue('o');
                    log.info("server host override given " + hostOverrideAuthority);
                }
            }
            if (cl.hasOption('i')) {
                id = cl.getOptionValue('i');
            }
        } catch (Exception e) {
            log.warn("cli parsing failed with exception", e);

        }
    }

    private void processEnvironmentOptions() {
        if (System.getenv().containsKey(CORE_SMARTCONTRACT_ID_NAME)) {
            this.id = System.getenv(CORE_SMARTCONTRACT_ID_NAME);
        }
        if (System.getenv().containsKey(CORE_NODE_ADDRESS)) {
            this.host = System.getenv(CORE_NODE_ADDRESS);
        }
        if (System.getenv().containsKey(CORE_NODE_TLS_ENABLED)) {
            this.tlsEnabled = Boolean.parseBoolean(System.getenv(CORE_NODE_TLS_ENABLED));
            if (System.getenv().containsKey(CORE_NODE_TLS_SERVERHOSTOVERRIDE)) {
                this.hostOverrideAuthority = System.getenv(CORE_NODE_TLS_SERVERHOSTOVERRIDE);
            }
            if (System.getenv().containsKey(CORE_NODE_TLS_ROOTCERT_FILE)) {
                this.rootCertFile = System.getenv(CORE_NODE_TLS_ROOTCERT_FILE);
            }
        }
    }

    public ManagedChannel newPeerClientConnection() {
        final NettyChannelBuilder builder = NettyChannelBuilder.forAddress(host, port);
        log.info("Configuring channel connection to peer.");

        if (tlsEnabled) {
            log.info("TLS is enabled");
            try {
                final SslContext sslContext = GrpcSslContexts.forClient().trustManager(new File(this.rootCertFile)).build();
                builder.negotiationType(NegotiationType.TLS);
                if (!hostOverrideAuthority.equals("")) {
                    log.info("Host override " + hostOverrideAuthority);
                    builder.overrideAuthority(hostOverrideAuthority);
                }
                builder.sslContext(sslContext);
                log.info("TLS context built: " + sslContext);
            } catch (SSLException e) {
                log.error("failed connect to peer with SSLException", e);
            }
        } else {
            builder.usePlaintext(true);
        }
        return builder.build();
    }

    public void chatWithPeer(ManagedChannel connection) {
        ChatStream chatStream = new ChatStream(connection, this);

        // Send the SmartContractID during register.
        SmartContractID smartcontractID = SmartContractID.newBuilder()
                .setName(id)
                .build();

        SmartContractMessage payload = SmartContractMessage.newBuilder()
                .setPayload(smartcontractID.toByteString())
                .setType(Type.REGISTER)
                .build();

        // Register on the stream
        log.info(String.format("Registering as '%s' ... sending %s", id, Type.REGISTER));
        chatStream.serialSend(payload);

        while (true) {
            try {
                chatStream.receive();
            } catch (Exception e) {
                log.error("Receiving message error", e);
                break;
            }
        }
    }

    protected static Response newSuccessResponse(String message, byte[] payload) {
        return new Response(SUCCESS, message, payload);
    }

    protected static Response newSuccessResponse() {
        return newSuccessResponse(null, null);
    }

    protected static Response newSuccessResponse(String message) {
        return newSuccessResponse(message, null);
    }

    protected static Response newSuccessResponse(byte[] payload) {
        return newSuccessResponse(null, payload);
    }

    protected static Response newErrorResponse(String message, byte[] payload) {
        return new Response(INTERNAL_SERVER_ERROR, message, payload);
    }

    protected static Response newErrorResponse() {
        return newErrorResponse(null, null);
    }

    protected static Response newErrorResponse(String message) {
        return newErrorResponse(message, null);
    }

    protected static Response newErrorResponse(byte[] payload) {
        return newErrorResponse(null, payload);
    }

    protected static Response newErrorResponse(Throwable throwable) {
        return newErrorResponse(throwable.getMessage(), printStackTrace(throwable));
    }

    private static byte[] printStackTrace(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        final StringWriter buffer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(buffer));
        return buffer.toString().getBytes(StandardCharsets.UTF_8);
    }

    public String getSmartContractID(){
        return this.id;
    }
}
