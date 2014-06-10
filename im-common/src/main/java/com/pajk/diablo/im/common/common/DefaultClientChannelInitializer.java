package com.pajk.diablo.im.common.common;

import java.net.URI;

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

/**
 * <pre>
 * Created by zhaoming on 14-6-5 下午9:06
 * </pre>
 */
public class DefaultClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private URI uri;

    public DefaultClientChannelInitializer(URI uri) {
        this.uri = uri;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-codec", new HttpClientCodec());// 这里可以再优化 -- Protobuf
        pipeline.addLast("aggregator", new HttpObjectAggregator(8192));
        pipeline.addLast("decoder", new Base64Decoder());
        pipeline.addLast("encoder", new Base64Encoder());
        pipeline.addLast("ws-handler",
                         new DefaultClientHandler(
                                                  WebSocketClientHandshakerFactory.newHandshaker(uri,
                                                                                                 WebSocketVersion.V13,
                                                                                                 null,
                                                                                                 false,
                                                                                                 new DefaultHttpHeaders())));
    }
}
