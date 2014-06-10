package com.pajk.diablo.im.server.core;

import com.pajk.diablo.im.server.handler.DefaultServerHandler;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.base64.Base64Decoder;
import io.netty.handler.codec.base64.Base64Encoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 上午9:58
 * </pre>
 */
public class DefaultServerInitializer extends ChannelInitializer<SocketChannel> {

    private Class<? extends ChannelInboundHandlerAdapter> channelInboundHandlerClass;

    public DefaultServerInitializer(Class<? extends ChannelInboundHandlerAdapter> channelInboundHandlerClass) {
        this.channelInboundHandlerClass = channelInboundHandlerClass;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("codec-http", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("decoder", new Base64Decoder());
        pipeline.addLast("encoder", new Base64Encoder());

        if (channelInboundHandlerClass == null) {
            channelInboundHandlerClass = DefaultServerHandler.class;
        }

        // 因为handle其中包含了一些成员变量，所以一个channel，建立一个handler实例
        pipeline.addLast("handler", channelInboundHandlerClass.newInstance());
    }
}
