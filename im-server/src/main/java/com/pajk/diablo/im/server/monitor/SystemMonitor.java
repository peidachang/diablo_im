package com.pajk.diablo.im.server.monitor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pajk.diablo.im.client.WSClient;
import com.pajk.diablo.im.common.store.ObjectBuilder;
import com.pajk.diablo.im.common.store.TairManagerProxy;
import com.pajk.diablo.im.common.util.KeyGenerate;
import com.pajk.diablo.im.server.core.RegisterManager;
import com.pajk.diablo.im.server.core.ServerConfig;
import com.pajk.diablo.im.server.core.SysApplicationContext;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 下午5:29
 * </pre>
 */
public class SystemMonitor implements Runnable {

    private static final Logger   logger                              = LoggerFactory.getLogger(SystemMonitor.class.getName());

    private static SystemMonitor  instance                            = new SystemMonitor();

    private SysApplicationContext wsApplication;

    public static final long      delayNum                            = 10;

    private final AtomicLong      clientCallTotalNums                 = new AtomicLong(0);                                     // 客户端数据处理的总数
    private final AtomicLong      clientCallFailNums                  = new AtomicLong(0);                                     // 客户端数据处理的失败数
    private final AtomicLong      clientCallSuccessNums               = new AtomicLong(0);                                     // 客户端数据处理的成功数
    private final AtomicLong      serverTransferCallTotalNums         = new AtomicLong(0);                                     // 服务端数据转发总数
    private final AtomicLong      serverTransferCallFailNums          = new AtomicLong(0);                                     // 服务端数据转发失败数
    private final AtomicLong      serverTransferCallSuccessNums       = new AtomicLong(0);                                     // 服务端数据转发成功数
    private final AtomicLong      serverTransferCallHandleFailNums    = new AtomicLong(0);                                     // 服务端数据转发处理失败数
    private final AtomicLong      serverTransferCallHandleSuccessNums = new AtomicLong(0);                                     // 服务端数据转发处理成功数

    private SystemMonitor() {
    }

    public static SystemMonitor getInstance() {
        return instance;
    }

    /**
     * 客户端数据处理的总数
     */
    public void increClientCallTotalNums() {
        clientCallTotalNums.incrementAndGet();
    }

    /**
     * 客户端数据处理的失败数
     */
    public void increClientCallFailNums() {
        clientCallFailNums.incrementAndGet();
    }

    /**
     * 客户端数据处理的成功数
     */
    public void increClientCallSuccessNums() {
        clientCallSuccessNums.incrementAndGet();
    }

    /**
     * 服务端数据转发总数
     */
    public void increServerTransferCallTotalNums() {
        serverTransferCallTotalNums.incrementAndGet();
    }

    /**
     * 服务端数据转发失败数
     */
    public void increServerTransferCallFailNums() {
        serverTransferCallFailNums.incrementAndGet();
    }

    /**
     * 服务端数据转发成功数
     */
    public void increServerTransferCallSuccessNums() {
        serverTransferCallSuccessNums.incrementAndGet();
    }

    /**
     * 服务端数据转发处理失败数
     */
    public void increServerTransferCallHandleFailNums() {
        serverTransferCallHandleFailNums.incrementAndGet();
    }

    /**
     * 服务端数据转发处理成功数
     */
    public void incrServerTransferCallHandleSuccessNums() {
        serverTransferCallHandleSuccessNums.incrementAndGet();
    }

    public void start() {

        wsApplication = SysApplicationContext.getInstance();
        ServerConfig serverConfig = wsApplication.getServerConfig();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this, delayNum, serverConfig.getMonitorPeriod(),
                                                     serverConfig.getMonitorTimeunit());
    }

    @Override
    public void run() {

        logger.debug(" -------- start SystemMonitor -------- ");

        // 1.客户端链接的处理,去除已经失效的连接
        clientChannelCheck();

        // 2.检测服务端之间的通信 -- 客户端连接
        serverClientChannelCheck();

        // 3.检测服务端之间的通信 -- 服务端连接
        serverChannelCheck();

        // 4.更新监控信息
        updateMonitorInfo();

        logger.debug(" -------- end SystemMonitor -------- ");
    }

    private void updateMonitorInfo() {
        // int initVersion = 1;
        final String uri = wsApplication.getServerConfig().getLocalMachineHost() + ":"
                           + +wsApplication.getServerConfig().getLocalMachineClientPort();

        TairManagerProxy tairManagerProxy = wsApplication.getTairManagerProxy();

        tairManagerProxy.concurrentHashMapPutSilence(KeyGenerate.getServerAggregateMonitorKey(),
                                                     KeyGenerate.monitorKeyGenerate(wsApplication.getServerConfig().getLocalMachineHost(),
                                                                                    Integer.valueOf(wsApplication.getServerConfig().getLocalMachineClientPort())),
                                                     new ObjectBuilder() {

                                                         @Override
                                                         public Serializable buildObject() {
                                                             MonitorVo monitorVo = new MonitorVo(
                                                                                                 uri,
                                                                                                 clientCallTotalNums.get(),
                                                                                                 clientCallFailNums.get(),
                                                                                                 clientCallSuccessNums.get(),
                                                                                                 serverTransferCallTotalNums.get(),
                                                                                                 serverTransferCallFailNums.get(),
                                                                                                 serverTransferCallSuccessNums.get(),
                                                                                                 serverTransferCallHandleFailNums.get(),
                                                                                                 serverTransferCallHandleSuccessNums.get(),
                                                                                                 wsApplication.loadAllClientChannel().size());

                                                             return monitorVo;
                                                         }
                                                     });

        // while (true) {
        //
        // Result<DataEntry> result = tairManagerProxy.get(KeyGenerate.getServerAggregateMonitorKey());
        //
        // HashMap<String, MonitorVo> monitorVoHashMap = null;
        // if (result.isSuccess()) {
        //
        // if (result.getValue() == null) {
        // monitorVoHashMap = new HashMap<String, MonitorVo>();
        // } else {
        // monitorVoHashMap = (HashMap<String, MonitorVo>) result.getValue().getValue();
        // }
        //
        // MonitorVo monitorVo = new MonitorVo(uri, clientCallTotalNums.get(), clientCallFailNums.get(),
        // clientCallSuccessNums.get(), serverTransferCallTotalNums.get(),
        // serverTransferCallFailNums.get(),
        // serverTransferCallSuccessNums.get(),
        // serverTransferCallHandleFailNums.get(),
        // serverTransferCallHandleSuccessNums.get(),
        // wsApplication.loadAllClientChannel().size());
        //
        // monitorVoHashMap.put(KeyGenerate.monitorKeyGenerate(wsApplication.getServerConfig().getLocalMachineHost(),
        // Integer.valueOf(wsApplication.getServerConfig().getLocalMachineClientPort())),
        // monitorVo);
        //
        // ResultCode put = tairManagerProxy.put(KeyGenerate.getServerAggregateMonitorKey(), monitorVoHashMap,
        // initVersion);
        // if (put != ResultCode.SUCCESS) {
        // initVersion++;
        // } else {
        // logger.debug("update  monitor info   from tair success! " + monitorVo.toString());
        // break;
        // }
        // } else {
        // logger.warn("load monitor info from tair error ");
        // break;
        // }
        //
        // }
    }

    private void serverChannelCheck() {
        HashMap<String, Channel> serverChannelHashMap = wsApplication.loadServerChannelChannel();
        for (Map.Entry<String, Channel> entry : serverChannelHashMap.entrySet()) {
            String key = entry.getKey();
            Channel channel = entry.getValue();
            if (channel == null || !channel.isOpen() || !channel.isActive()) {
                logger.info("remove server channel : {}", key);
                wsApplication.removeServerChannel(key);
            }

        }

        // logger.debug("server channel nums:" + serverChannelHashMap.size());
    }

    private void serverClientChannelCheck() {
        HashMap<String, WSClient> serverClientHashMap = wsApplication.loadServerClientChannel();
        for (Map.Entry<String, WSClient> channelEntry : serverClientHashMap.entrySet()) {
            String key = channelEntry.getKey();
            WSClient client = channelEntry.getValue();

            if (client == null || !client.isOpen()) {
                logger.info("remove server client : {}", key);
                wsApplication.removeServerClient(key);
            }

            try {
                // 心跳检测
                WebSocketFrame frame = new PingWebSocketFrame(Unpooled.copiedBuffer(new byte[] { 8, 1, 8, 1 }));
                client.sendMsg(frame);

            } catch (Exception e) {
                client.close();
                logger.error("remove server client error ", e);
                wsApplication.removeServerClient(key);
            }

        }

        // logger.debug("server client nums:" + serverClientHashMap.size());
    }

    private void clientChannelCheck() {
        HashMap<String, Channel> channelHashMap = wsApplication.loadAllClientChannel();
        Set<Map.Entry<String, Channel>> entries = channelHashMap.entrySet();

        for (Map.Entry<String, Channel> entry : entries) {
            String userId = entry.getKey();
            Channel channel = entry.getValue();
            if (channel == null || !channel.isOpen() || !channel.isActive()) {
                logger.debug("remove client: {}", userId);
                RegisterManager.unRegisterUser(userId);
            }

        }
        // logger.debug("clients nums:" + channelHashMap.size());
    }
}
