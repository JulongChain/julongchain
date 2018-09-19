/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.bcia.julongchain.common.ssl.snoop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextGMBuilder;
import org.bcia.julongchain.common.ssl.SslContextGMBuilderTest;

import java.net.URI;

/**
 * 一个简单的HTTP客户端，用来打印http响应的内容，用于测试HttpSnoopServer类
 * @author cuilf
 * @date 2019/09/14
 * @company InfosecTechnology
 */
public final class HttpSnoopClient {

    static final String URL = System.getProperty("url", "http://127.0.0.1:8080/");

    public static void main(String[] args) throws Exception {
        URI uri = new URI(URL);
        String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // 配置 SSL context.
        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextGMBuilder.forClient()
                .trustManager(SslContextGMBuilderTest.TRUST_CERT)
                .keyManager(SslContextGMBuilderTest.ENC_CERT, SslContextGMBuilderTest.ENC_KEY, SslContextGMBuilderTest.SIGN_CERT, SslContextGMBuilderTest.SIGN_KEY, null)
                .build();
        } else {
            sslCtx = null;
        }

        // 配置客户端 client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new HttpSnoopClientInitializer(sslCtx));

            // 尝试连接.
            Channel ch = b.connect(host, port).sync().channel();

            // 准备HTTP请求参数.
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

            // 设置演示用的 cookies.
            request.headers().set(
                    HttpHeaderNames.COOKIE,
                    ClientCookieEncoder.STRICT.encode(
                            new DefaultCookie("my-cookie", "foo"),
                            new DefaultCookie("another-cookie", "bar")));

            // 发送 HTTP 请求.
            ch.writeAndFlush(request);

            // 等待服务器关闭连接.
            ch.closeFuture().sync();
        } finally {
            // 关闭执行线程来退出客户端.
            group.shutdownGracefully();
        }
    }
}
