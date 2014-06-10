package com.pajk.diablo.im.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pajk.diablo.im.common.util.NetUtils;
import com.pajk.diablo.im.server.handler.Server4ServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 上午9:57
 * </pre>
 */
public class NIOServer {

    private static final Logger               logger = LoggerFactory.getLogger(NIOServer.class.getName());

    private ChannelInitializer<SocketChannel> channelInitializer;
    private int                               port;
    private String                            serverName;

    public NIOServer(ChannelInitializer channelInitializer, int port, String serverName) {
        this.channelInitializer = channelInitializer;
        this.port = port;
        this.serverName = serverName;
    }

    public void run() throws Exception {

        // int port = WSApplication.getInstance().getServerConfig().getLocalMachineClientPort();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(channelInitializer);
            b.childOption(ChannelOption.SO_KEEPALIVE, true); // 保持长链接
            b.childOption(ChannelOption.TCP_NODELAY, false);// 因为对于时延要求比较高
            b.childOption(ChannelOption.SO_RCVBUF, 256 * 1024);// 接收缓存大小 256K
            b.childOption(ChannelOption.SO_SNDBUF, 256 * 1024);// 发送缓存大小 256K

            Channel ch = b.bind(port).sync().channel();

            logger.info("Web socket server started at  {}  {}: ", NetUtils.loadRealIp(), port);
            // logger.info("Open your browser and navigate to http://localhost:" + port + '/');

            ch.closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public String getServerName() {
        return serverName;
    }

    public static void main(String[] args) throws Exception {
        // int port;
        // if (args.length > 0) {
        // port = Integer.parseInt(args[0]);
        // } else {
        // port = 8080;
        // }
        // LoggerBO.initLog();

        ChannelInitializer channelInitializer = new DefaultServerInitializer(Server4ServerHandler.class);
        int port = 8080;
        String serverName = "clientServer";
        new NIOServer(channelInitializer, port, serverName).run();
    }
}
