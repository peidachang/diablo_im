package com.pajk.diablo.im.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.pajk.diablo.im.common.common.AbstractClientHandler;
import com.pajk.diablo.im.common.common.DefaultClientChannelInitializer;
import com.pajk.diablo.im.common.util.LogUtils;
import com.pajk.diablo.im.common.util.NetUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * <pre>
 * Created by zhaoming on 14-5-28 上午9:27
 * </pre>
 */
public class WSClient {

    private static final Logger               logger = LoggerFactory.getLogger(WSClient.class.getName());

    private String                            name;
    private URI                               uri;

    private Channel                           channle;
    private AbstractClientHandler             handler;
    private ChannelInitializer<SocketChannel> channelInitializer;
    private EventLoopGroup                    group;

    public WSClient(URI uri) {
        this.uri = uri;
    }

    public static WSClient newBuilder(URI uri) {
        return new WSClient(uri);
    }

    public WSClient withUri(URI uri) {
        this.uri = uri;
        return this;
    }

    public WSClient withWSClientHandler(AbstractClientHandler handler) {
        this.handler = handler;
        return this;
    }

    public WSClient withChannelInitializer(ChannelInitializer<SocketChannel> channelInitializer) {
        this.channelInitializer = channelInitializer;
        return this;
    }

    public WSClient withName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public ChannelFuture sendMsg(String msg) throws Exception {

        Preconditions.checkNotNull(msg, "msg is null!");

        if (!channle.isOpen()) {
            establish();
        }

        // 第一期先用文本传输，然后再改进为二进制流
        WebSocketFrame frame = new TextWebSocketFrame(msg);
        return channle.writeAndFlush(frame);
    }

    public ChannelFuture sendMsg(WebSocketFrame msg) throws Exception {

        Preconditions.checkNotNull(msg, "msg is null!");

        if (!channle.isOpen()) {
            establish();
        }

        return channle.writeAndFlush(msg);
    }

    public Channel establish() throws Exception {

        // Preconditions.checkNotNull(handler, "handler is null!");
        Preconditions.checkNotNull(channelInitializer, "channelInitializer is null!");

        group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).handler(channelInitializer);

        int port = uri.getPort();
        if (uri.getPort() == -1) {
            port = NetUtils.loadDefaultPort(uri);
        }

        channle = b.connect(uri.getHost(), port).sync().channel();

        Preconditions.checkNotNull(channle, "establish failed!" + uri.toString());

        logger.info("has connected to server -- {} : {}", uri.getHost(), port);

        // channle.closeFuture().sync();
        // handler.handshakeFuture().sync();

        return channle;
    }

    /**
     * 是否是有效的通道
     * 
     * @return
     */
    public boolean isOpen() {
        return (channle == null || !channle.isOpen()) ? false : true;
    }

    public void close() {
        if (group != null) {
            try {
                channle.closeFuture().sync();
                group.shutdownGracefully();
            } catch (InterruptedException e) {
                logger.error("close connection error : {} ", name, e);
            }

        }
    }

    public static void main(String[] args) throws Exception {

        LogUtils.initLogback();
        // URI uri = new URI("ws://10.0.128.147:7070/websocket");
        URI uri = new URI("ws://127.0.0.1:5050/websocket");

        // final AbstractClientHandler handler = new WSDefaultClientHandler(
        // WebSocketClientHandshakerFactory.newHandshaker(uri,
        // WebSocketVersion.V13,
        // null,
        // false,
        // new DefaultHttpHeaders()));

        // ChannelInitializer wsChannelInitializer = ChannelInitializerFactory.getWSChannelInitializer(uri, handler);

        // ChannelInitializer serverChannelInitializer = new DefaultServerInitializer(Server4ServerHandler.class);

        // ChannelInitializer wsChannelInitializer = new DefaultClientInitializer(uri);

        DefaultClientChannelInitializer wsChannelInitializer = new DefaultClientChannelInitializer(uri);

        WSClient client = WSClient.newBuilder(uri).withChannelInitializer(wsChannelInitializer);

        Channel ch = client.establish();

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String msg = console.readLine();
            if (msg == null) {
                break;
            } else if ("bye".equals(msg.toLowerCase())) {
                ch.writeAndFlush(new CloseWebSocketFrame());
                ch.closeFuture().sync();
                break;
            } else if ("ping".equals(msg.toLowerCase())) {
                WebSocketFrame frame = new PingWebSocketFrame(Unpooled.copiedBuffer(new byte[] { 8, 1, 8, 1 }));
                ch.writeAndFlush(frame);
            } else if ("test".equals(msg.toLowerCase())) {
                // boolean open = ch.isOpen();
                logger.info("is open:" + ch.isOpen());
                logger.info("is isRegistered:" + ch.isRegistered());
                logger.info("is isActive:" + ch.isActive());
            } else {
                WebSocketFrame frame = new TextWebSocketFrame(msg);
                ch.writeAndFlush(frame);
            }
        }

    }
}
