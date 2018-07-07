/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.julongchain.core.smartcontract.shim;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.core.smartcontract.shim.impl.ChatStream;
import org.bcia.julongchain.protos.node.SmartContractShim;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public abstract class SmartContractBase implements ISmartContract {

  @Override
  public abstract SmartContractResponse init(ISmartContractStub stub);

  @Override
  public abstract SmartContractResponse invoke(ISmartContractStub stub);

  @Override
  public abstract String getSmartContractStrDescription();

  private static Log logger = LogFactory.getLog(SmartContractBase.class);

  public static final String DEFAULT_HOST = "127.0.0.1";
  public static final int DEFAULT_PORT = 7051;

  private String host = DEFAULT_HOST;
  private int port = DEFAULT_PORT;
  private String hostOverrideAuthority = "";
  private boolean tlsEnabled = false;
  private String rootCertFile = "/etc/hyperledger/fabric/peer.crt";

  protected String id;

  private static final String CORE_SMARTCONTRACT_ID_NAME = "CORE_SMARTCONTRACT_ID_NAME";
  private static final String CORE_PEER_ADDRESS = "CORE_PEER_ADDRESS";
  private static final String CORE_PEER_TLS_ENABLED = "CORE_PEER_TLS_ENABLED";
  private static final String CORE_PEER_TLS_SERVERHOSTOVERRIDE = "CORE_PEER_TLS_SERVERHOSTOVERRIDE";
  private static final String CORE_PEER_TLS_ROOTCERT_FILE = "CORE_PEER_TLS_ROOTCERT_FILE";

  /**
   * Start smartcontract
   *
   * @param args command line arguments
   */
  public void start(String[] args) {
    processEnvironmentOptions();
    processCommandLineOptions(args);
    if (this.id == null) {
      logger.error(
          String.format(
              "The smartcontract id must be specified using either the -i or --i command line options or the %s environment variable.",
              CORE_SMARTCONTRACT_ID_NAME));
    }
    new Thread(
            () -> {
              logger.trace("smartcontract started");
              final ManagedChannel connection = newPeerClientConnection();
              logger.trace("connection created");
              chatWithPeer(connection);
              logger.trace("chatWithPeer DONE");
            })
        .start();
  }

  private void processCommandLineOptions(String[] args) {
    Options options = new Options();
    options.addOption("a", "peer.address", true, "Address of peer to connect to");
    options.addOption(null, "peerAddress", true, "Address of peer to connect to");
    options.addOption("s", "securityEnabled", false, "Present if security is enabled");
    options.addOption("i", "id", true, "Identity of smartcontract");
    options.addOption("o", "hostNameOverride", true, "Hostname override for server certificate");
    try {
      CommandLine cl = new DefaultParser().parse(options, args);
      if (cl.hasOption("peerAddress") || cl.hasOption('a')) {
        if (cl.hasOption('a')) {
          host = cl.getOptionValue('a');
        } else {
          host = cl.getOptionValue("peerAddress");
        }
        port = new Integer(host.split(":")[1]);
        host = host.split(":")[0];
      }
      if (cl.hasOption('s')) {
        tlsEnabled = true;
        logger.info("TLS enabled");
        if (cl.hasOption('o')) {
          hostOverrideAuthority = cl.getOptionValue('o');
          logger.info("server host override given " + hostOverrideAuthority);
        }
      }
      if (cl.hasOption('i')) {
        id = cl.getOptionValue('i');
      }
    } catch (Exception e) {
      logger.warn("cli parsing failed with exception", e);
    }
  }

  private void processEnvironmentOptions() {
    if (System.getenv().containsKey(CORE_SMARTCONTRACT_ID_NAME)) {
      this.id = System.getenv(CORE_SMARTCONTRACT_ID_NAME);
    }
    if (System.getenv().containsKey(CORE_PEER_ADDRESS)) {
      this.host = System.getenv(CORE_PEER_ADDRESS);
    }
    if (System.getenv().containsKey(CORE_PEER_TLS_ENABLED)) {
      this.tlsEnabled = Boolean.parseBoolean(System.getenv(CORE_PEER_TLS_ENABLED));
      if (System.getenv().containsKey(CORE_PEER_TLS_SERVERHOSTOVERRIDE)) {
        this.hostOverrideAuthority = System.getenv(CORE_PEER_TLS_SERVERHOSTOVERRIDE);
      }
      if (System.getenv().containsKey(CORE_PEER_TLS_ROOTCERT_FILE)) {
        this.rootCertFile = System.getenv(CORE_PEER_TLS_ROOTCERT_FILE);
      }
    }
  }

  public ManagedChannel newPeerClientConnection() {
    final NettyChannelBuilder builder =
            NettyChannelBuilder.forAddress(host, port).maxInboundMessageSize(CommConstant.MAX_GRPC_MESSAGE_SIZE);
    logger.info("Configuring channel connection to peer.");

    if (tlsEnabled) {
      logger.info("TLS is enabled");
      try {
        final SslContext sslContext =
            GrpcSslContexts.forClient().trustManager(new File(this.rootCertFile)).build();
        builder.negotiationType(NegotiationType.TLS);
        if (!hostOverrideAuthority.equals("")) {
          logger.info("Host override " + hostOverrideAuthority);
          builder.overrideAuthority(hostOverrideAuthority);
        }
        builder.sslContext(sslContext);
        logger.info("TLS context built: " + sslContext);
      } catch (SSLException e) {
        logger.error("failed connect to peer with SSLException", e);
      }
    } else {
      builder.usePlaintext(true);
    }
    return builder.build();
  }

  public void chatWithPeer(ManagedChannel connection) {
    ChatStream chatStream = new ChatStream(connection, this);

    logger.info("smartcontract id: " + id);

    // Send the SmartContractID during register.
    org.bcia.julongchain.protos.node.SmartContractPackage.SmartContractID smartcontractID =
        org.bcia.julongchain.protos.node.SmartContractPackage.SmartContractID.newBuilder()
            .setName(id)
            .build();

    SmartContractShim.SmartContractMessage payload =
        SmartContractShim.SmartContractMessage.newBuilder()
            .setPayload(smartcontractID.toByteString())
            .setType(SmartContractShim.SmartContractMessage.Type.REGISTER)
            .build();

    // Register on the stream
    logger.info(
        String.format(
            "Registering as '%s' ... sending %s",
            id, SmartContractShim.SmartContractMessage.Type.REGISTER));
    chatStream.serialSend(payload);

    while (true) {
      try {
        chatStream.receive();
      } catch (Exception e) {
        logger.error("Receiving message error", e);
        break;
      }
    }
  }

  protected static SmartContractResponse newSuccessResponse(String message, byte[] payload) {
    return new SmartContractResponse(SmartContractResponse.Status.SUCCESS, message, payload);
  }

  protected static SmartContractResponse newSuccessResponse() {
    return newSuccessResponse(null, null);
  }

  protected static SmartContractResponse newSuccessResponse(String message) {
    return newSuccessResponse(message, null);
  }

  protected static SmartContractResponse newSuccessResponse(byte[] payload) {
    return newSuccessResponse(null, payload);
  }

  protected static SmartContractResponse newErrorResponse(String message, byte[] payload) {
    return new SmartContractResponse(
        SmartContractResponse.Status.INTERNAL_SERVER_ERROR, message, payload);
  }

  protected static SmartContractResponse newErrorResponse() {
    return newErrorResponse(null, null);
  }

  protected static SmartContractResponse newErrorResponse(String message) {
    return newErrorResponse(message, null);
  }

  protected static SmartContractResponse newErrorResponse(byte[] payload) {
    return newErrorResponse(null, payload);
  }

  protected static SmartContractResponse newErrorResponse(Throwable throwable) {
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

  public String getSmartContractID() {
    return this.id;
  }
}
