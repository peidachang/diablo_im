package com.pajk.diablo.im.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pajk.diablo.im.common.util.LogUtils;
import com.pajk.diablo.im.server.handler.DefaultServerHandler;
import com.pajk.diablo.im.server.handler.Server4ServerHandler;
import com.pajk.diablo.im.server.monitor.SystemMonitor;
import io.netty.channel.ChannelInitializer;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 下午3:33
 * </pre>
 */
public class ServerFacade {

    private static final Logger logger = LoggerFactory.getLogger(ServerFacade.class.getName());

    public synchronized void init() throws Exception {

        logger.info("----------- system init start -----------");

        // 1.初始化上下文
        SysApplicationContext wsApplication = SysApplicationContext.getInstance();

        // 2.加载配置文件
        InitOperation initOperation = new InitOperation();
        final ServerConfig wsServerConfig = initOperation.init();
        wsApplication.setWsServerConfig(wsServerConfig);

        // 3.客户端同服务短的WS服务
        Thread clientThread = new Thread(new Runnable() {

            @Override
            public void run() {
                ChannelInitializer clientChannelInitializer = new DefaultServerInitializer(DefaultServerHandler.class);
                try {
                    new NIOServer(clientChannelInitializer, wsServerConfig.getLocalMachineClientPort(),
                                  "server-4-client").run();
                } catch (Exception e) {
                    logger.error("init server-4-client error !", e);
                    System.exit(-1);
                }
            }
        });

        clientThread.start();

        // 4.服务端之间的WS服务
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                ChannelInitializer serverChannelInitializer = new DefaultServerInitializer(Server4ServerHandler.class);
                try {
                    new NIOServer(serverChannelInitializer, wsServerConfig.getLocalMachineServerPort(),
                                  "server-4-server").run();
                } catch (Exception e) {
                    logger.error("init server-4-server error !", e);
                    System.exit(-1);
                }
            }
        });

        serverThread.start();

        // 5.wsApplication 中失效channel的监控清楚
        SystemMonitor.getInstance().start();

        logger.info("----------- system init end -----------");
    }

    public static void main(String[] args) throws Exception {

        // LoggerBO.initLog();
        LogUtils.initLogback();
        ServerFacade wsServerFacade = new ServerFacade();
        wsServerFacade.init();

    }
}
