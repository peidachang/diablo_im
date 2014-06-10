package com.pajk.diablo.im.server.core;

import java.net.URI;

import com.pajk.diablo.im.common.enums.RequestTypeEnum;
import com.pajk.diablo.im.server.command.MessageCammandFactory;
import com.pajk.diablo.im.server.handler.DefaultServerClientHandler;

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
 * Created by zhaoming on 14-6-6 下午4:19
 * </pre>
 */
public class DefaultServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private URI             uri;

    private RequestTypeEnum requestTypeEnum;

    public DefaultServerChannelInitializer(URI uri, RequestTypeEnum requestTypeEnum) {
        this.uri = uri;
        this.requestTypeEnum = requestTypeEnum;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-codec", new HttpClientCodec());// 这里可以再优化 -- Protobuf
        pipeline.addLast("aggregator", new HttpObjectAggregator(8192));
        pipeline.addLast("decoder", new Base64Decoder());
        pipeline.addLast("encoder", new Base64Encoder());
        pipeline.addLast("ws-handler",
                         new DefaultServerClientHandler(
                                                 WebSocketClientHandshakerFactory.newHandshaker(uri,
                                                                                                WebSocketVersion.V13,
                                                                                                null,
                                                                                                false,
                                                                                                new DefaultHttpHeaders()),
                                                 MessageCammandFactory.getCommand(requestTypeEnum)));
    }
}
