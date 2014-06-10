package com.pajk.diablo.im.client;

import java.net.URI;

import javax.net.ssl.SSLEngine;

import com.pajk.diablo.im.common.common.AbstractClientHandler;
import com.pajk.diablo.im.common.common.DefaultClientHandler;
import com.pajk.diablo.im.common.common.WebSocketSslClientContextFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.base64.Base64Decoder;
import io.netty.handler.codec.base64.Base64Encoder;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslHandler;

/**
 * <pre>
 * Created by zhaoming on 14-5-28 下午1:52
 * todo 待删除
 * </pre>
 */
public class ChannelInitializerFactory {

    public static ChannelInitializer getWSChannelInitializer(final URI uri, final AbstractClientHandler handler) {

        final String protocol = uri.getScheme();

        ChannelInitializer<SocketChannel> initializer;

        // Normal WebSocket
        if ("ws".equals(protocol)) {
            initializer = new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("http-codec", new HttpClientCodec());
                    pipeline.addLast("aggregator", new HttpObjectAggregator(8192));
                    pipeline.addLast("decoder", new Base64Decoder());
                    pipeline.addLast("encoder", new Base64Encoder());// 这里可以再优化 -- Protobuf
                    pipeline.addLast("ws-handler",
                                     new DefaultClientHandler(
                                                              WebSocketClientHandshakerFactory.newHandshaker(uri,
                                                                                                             WebSocketVersion.V13,
                                                                                                             null,
                                                                                                             false,
                                                                                                             new DefaultHttpHeaders())));
                }
            };

            // Secure WebSocket
        } else if ("wss".equals(protocol)) {
            initializer = new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    SSLEngine engine = WebSocketSslClientContextFactory.getContext().createSSLEngine();
                    engine.setUseClientMode(true);

                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addFirst("ssl", new SslHandler(engine));
                    pipeline.addLast("http-codec", new HttpClientCodec());
                    pipeline.addLast("aggregator", new HttpObjectAggregator(8192)).addLast("ws-handler", handler);
                }
            };

        } else {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }

        return initializer;

    }
}
