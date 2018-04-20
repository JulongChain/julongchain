package org.bcia.javachain.common.ssl;

/*
 * Copyright Infosec. All Rights Reserved.
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

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.gossip.Node1;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * @purpose 演示SSLEngine类的用法
 * @company Infosec Technology
 * @auther clf
 * @date 18-4-20
 */
public class SSLEngineDemo {

    private static JavaChainLog log = JavaChainLogFactory.getLog(SSLEngineDemo.class);

    public static void main(String[] args) {
        try {
            doIt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void doIt() throws Exception {
        char[] password = "ABCDEFG".toCharArray();
        // 创建KeyStore和TrustStore
        KeyStore storeKey = KeyStore.getInstance("JKS");
        storeKey.load(new FileInputStream("testKeys"), password);

        KeyStore storeTrust = KeyStore.getInstance("JKS");
        storeTrust.load(new FileInputStream("testTrusts"), password);
        //
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(storeKey, password);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(storeTrust);
        // 创建并初始化SSLContext,最终得到SSLEngine
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        SSLEngine sslEngine = sslContext.createSSLEngine("testHost", 443);
        sslEngine.setUseClientMode(true);
        // 创建NIO的SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("testHost", 443));
        // 保证连接到对端
        while (!socketChannel.finishConnect()) {
            // keep connection....
            log.info("connecting...");
        }
        // 分配应用buffer和网络buffer
        SSLSession sslSession = sslEngine.getSession();
        ByteBuffer myAppBuffer = ByteBuffer.allocate(sslSession.getApplicationBufferSize());
        ByteBuffer myNetBuffer = ByteBuffer.allocate(sslSession.getPacketBufferSize());

        ByteBuffer peerAppBuffer = ByteBuffer.allocate(sslSession.getApplicationBufferSize());
        ByteBuffer peerNetBuffer = ByteBuffer.allocate(sslSession.getPacketBufferSize());

        // 初始化 handshake
        doHandshake(socketChannel, sslEngine, myNetBuffer, peerNetBuffer);

        // 发送一个测试数据
        myAppBuffer.put("hello".getBytes());
        myAppBuffer.flip();

        while (myAppBuffer.hasRemaining()) {
            // 使用sslEngine包装AppBuffer的内容，包装完的数据存储在NetBuffer中
            SSLEngineResult res = sslEngine.wrap(myAppBuffer, myNetBuffer);
            // 处理wrap的包装结果
            if (res.getStatus() == SSLEngineResult.Status.OK) {
                // wrap成功，清空AppBuffer
                myAppBuffer.compact();

                // 使用socketChannel发送被加密的数据到对端
                while(myNetBuffer.hasRemaining()) {
                    int num = socketChannel.write(myNetBuffer);
                    if (num == 0) {
                        // 发送失败...
                    }
                }
            }

            // Handle other status:  BUFFER_OVERFLOW, CLOSED...
        }


        // 从对端读取数据，存储到peerNetBuffer中
        int num = socketChannel.read(peerNetBuffer);
        if (num == -1) {
            // The channel has reached end-of-stream
        } else if (num == 0) {
            // No bytes read; try again ...
        } else {
            // 读取到了对端数据，开始处理
            peerNetBuffer.flip();
            SSLEngineResult res = sslEngine.unwrap(peerNetBuffer, peerAppBuffer);

            if (res.getStatus() == SSLEngineResult.Status.OK) {
                peerNetBuffer.compact();

                if (peerAppBuffer.hasRemaining()) {
                    // 处理读取到的明文
                }
            } else if (res.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                // Maybe need to enlarge the peer application data buffer.
                if (sslEngine.getSession().getApplicationBufferSize() > peerAppBuffer.capacity()) {
                    // enlarge the peer application data buffer
                } else {
                    // compact or clear the buffer
                }
                // retry the operation
            } else if (res.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                // Maybe need to enlarge the peer network packet buffer
                if (sslEngine.getSession().getPacketBufferSize() > peerNetBuffer.capacity()) {
                    // enlarge the peer network packet buffer
                } else {
                    // compact or clear the buffer
                }
                // obtain more inbound network data and then retry the operation
            }
            // Handle other status: CLOSED...
        }




        // Indicate that application is done with engine
        sslEngine.closeOutbound();

        while (!sslEngine.isOutboundDone()) {
            // Get close message
            SSLEngineResult res = sslEngine.wrap(ByteBuffer.allocate(0), myNetBuffer);

            // Check res statuses

            // Send close message to peer
            while(myNetBuffer.hasRemaining()) {
                int length = socketChannel.write(myNetBuffer);
                if (length == 0) {
                    // no bytes written; try again later
                }
                myNetBuffer.compact();
            }
        }

        // Close transport
        socketChannel.close();

    }

    private static void doHandshake(SocketChannel socketChannel, SSLEngine engine, ByteBuffer myNetBuffer, ByteBuffer peerNetBuffer) throws Exception {

        // Create byte buffers to use for holding application data
        int appBufferSize = engine.getSession().getApplicationBufferSize();
        ByteBuffer myAppData = ByteBuffer.allocate(appBufferSize);
        ByteBuffer peerAppData = ByteBuffer.allocate(appBufferSize);

        // Begin handshake
        engine.beginHandshake();
        SSLEngineResult.HandshakeStatus hs = engine.getHandshakeStatus();

        // Process handshaking message
        while (hs != SSLEngineResult.HandshakeStatus.FINISHED && hs != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            switch (hs) {
                case NEED_UNWRAP:
                    // Receive handshaking data from peer
                    if (socketChannel.read(peerNetBuffer) < 0) {
                        // The channel has reached end-of-stream
                    }

                    // Process incoming handshaking data
                    peerNetBuffer.flip();
                    SSLEngineResult res = engine.unwrap(peerNetBuffer, peerAppData);
                    peerNetBuffer.compact();
                    hs = res.getHandshakeStatus();

                    // Check status
                    switch (res.getStatus()) {
                        case OK :
                            // Handle OK status
                            break;

                        // Handle other status: BUFFER_UNDERFLOW, BUFFER_OVERFLOW, CLOSED...
                    }
                    break;

                case NEED_WRAP :
                    // Empty the local network packet buffer.
                    myNetBuffer.clear();

                    // Generate handshaking data
                    res = engine.wrap(myAppData, myNetBuffer);
                    hs = res.getHandshakeStatus();

                    // Check status
                    switch (res.getStatus()) {
                        case OK :
                            myNetBuffer.flip();

                            // Send the handshaking data to peer
                            while (myNetBuffer.hasRemaining()) {
                                socketChannel.write(myNetBuffer);
                            }
                            break;

                        // Handle other status:  BUFFER_OVERFLOW, BUFFER_UNDERFLOW, CLOSED...
                    }
                    break;

                case NEED_TASK :
                    // Handle blocking tasks
                    Runnable task;
                    while ((task = engine.getDelegatedTask()) != null) {
                        new Thread(task).start();
                    }
                    break;

                // Handle other status:  // FINISHED or NOT_HANDSHAKING...
            }
        }

        // Processes after handshaking...
    }
}
