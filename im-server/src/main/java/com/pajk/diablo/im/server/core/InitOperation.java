package com.pajk.diablo.im.server.core;

import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pajk.diablo.im.common.store.TairManagerProxy;
import com.pajk.diablo.im.common.util.KeyGenerate;
import com.pajk.diablo.im.common.util.NetUtils;
import com.pajk.diablo.im.common.util.PropertiesUtils;
import com.pajk.diablo.im.server.enums.TimeUnitEnums;
import com.pajk.diablo.im.server.vo.RemoteServerVo;

/**
 * <pre>
 * Created by zhaoming on 14-5-28 上午10:48
 * 初始化的一些工作
 * 1.加载配置文件
 * 2.初始化系统监控
 * 3.初始化tair
 * </pre>
 */
public class InitOperation {

    private static final Logger logger            = LoggerFactory.getLogger(InitOperation.class.getName());

    private static final String CLIENT_PORT       = "client.port";                                         // 本机暴露给客户端的端口
    private static final String SERVER_PORT       = "server.port";                                         // 本机暴露给服务端的端口
    private static final String SERVE_LIST        = "server.list";                                         // 集群地址
    private static final String MONITOR_PERIOD    = "monitor.period";                                      // 监控运行周期
    private static final String MONITOR_TIMEUNIT  = "monitor.timeunit";                                    // 监控运行的单位时间

    private static final long   defaultPeriod     = 10;                                                    // 运行周期的默认值
    private static final int    defaultClientPort = 8080;                                                  // 本机暴露给客户端的默认端口
    private static final int    defaultServerPort = 8081;                                                  // 本机暴露给客户端的默认端口

    public ServerConfig init() throws Exception {

        ServerConfig wsServerConfig = ServerConfig.newBuilder();

        // 1. 根据操作系统获取不同的配置路径
        Properties properties = PropertiesUtils.getProperties();

        // 2.加载本机的端口和地址
        initPort(wsServerConfig, properties);

        // 3.初始化tair
        TairManagerProxy proxy = TairManagerProxy.newBuilder().withProperties(properties).build();
        SysApplicationContext.getInstance().withTairManagerProxy(proxy);

        // 4.加载集群服务端配置的端口和地址
        initServerAggregate(wsServerConfig, properties);

        // 5. 加载监控运行参数
        initMonitor(wsServerConfig, properties);

        return wsServerConfig;
    }

    private void initMonitor(ServerConfig wsServerConfig, Properties properties) {
        long periodLong = defaultPeriod;
        String period = properties.getProperty(MONITOR_PERIOD);

        if (StringUtils.isNotBlank(period) && NumberUtils.isNumber(period.trim())) {
            periodLong = Long.valueOf(period.trim());
        }

        String mTimeUnit = properties.getProperty(MONITOR_TIMEUNIT);
        TimeUnit timeUnit = TimeUnitEnums.loadTimeUnit(mTimeUnit);

        // todo 可删除 自动注册
        logger.info("monitor period : {} . unit : {}", period, mTimeUnit);
        wsServerConfig.withMonitorTimeunit(timeUnit).withMonitorPeriod(periodLong);
    }

    private void initServerAggregate(final ServerConfig wsServerConfig, Properties properties) throws Exception {
        String serverList = properties.getProperty(SERVE_LIST);
        // Preconditions.checkNotNull(serverList,
        // "serverList is null,please check /home/admin/im-config/im-server.properties file!");

        logger.info("server Aggregate: {} ", serverList);

        // Preconditions.checkArgument(CollectionUtils.isNotEmpty(serverLists),
        // "serverList is null,please check /home/admin/im-config/im-server.properties file!");

        TairManagerProxy tairManagerProxy = SysApplicationContext.getInstance().getTairManagerProxy();

        RemoteServerVo remoteServerVo = new RemoteServerVo();
        remoteServerVo.setMachineHost(wsServerConfig.getLocalMachineHost());
        remoteServerVo.setMachinePort(wsServerConfig.getLocalMachineServerPort());

        tairManagerProxy.concurrentHashMapPutSilence(KeyGenerate.getServerAggregateKey4Tair(),
                                                     KeyGenerate.serverAggregateKeyGenerate(wsServerConfig.getLocalMachineHost(),
                                                                                            Integer.valueOf(wsServerConfig.getLocalMachineServerPort())),
                                                     remoteServerVo);
        // int initVersion = 1;
        // while (true) {
        // Result<DataEntry> dataEntryResult = tairManagerProxy.get(KeyGenerate.getServerAggregateKey4Tair());
        //
        // if (dataEntryResult.isSuccess()) {
        //
        // HashMap<String, RemoteServerVo> serverMap = null;
        // if (dataEntryResult.getValue() == null || dataEntryResult.getValue().getValue() == null) {
        // serverMap = new HashMap<String, RemoteServerVo>();
        // } else {
        // serverMap = (HashMap<String, RemoteServerVo>) dataEntryResult.getValue().getValue();
        // }
        //
        // RemoteServerVo remoteServerVo = new RemoteServerVo();
        // remoteServerVo.setMachineHost(wsServerConfig.getLocalMachineHost());
        // remoteServerVo.setMachinePort(wsServerConfig.getLocalMachineServerPort());
        // serverMap.put(KeyGenerate.serverAggregateKeyGenerate(wsServerConfig.getLocalMachineHost(),
        // Integer.valueOf(wsServerConfig.getLocalMachineServerPort())),
        // remoteServerVo);
        //
        // ResultCode put = tairManagerProxy.put(KeyGenerate.getServerAggregateKey4Tair(), serverMap, initVersion);
        // if (put != ResultCode.SUCCESS) {
        // initVersion++;
        // } else {
        // logger.info("add server aggregate to tair success:" + remoteServerVo.toString());
        // break;
        // }
        //
        // } else {
        //
        // logger.error("load server Aggregate info from tair error !");
        // throw new Exception("load server Aggregate info from tair error !");
        // }
        // }

        // List<String> serverLists = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(serverList);

        // Map<String, RemoteServerVo> serverMap = new HashMap<String, RemoteServerVo>(serverLists.size());
        // for (String server : serverLists) {
        //
        // RemoteServerVo remoteServerVo = new RemoteServerVo();
        // List<String> url = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(server);
        // if (CollectionUtils.isEmpty(url) || url.size() != 2) {
        // logger.error("init server :" + server + " error !");
        // continue;
        // }
        //
        // remoteServerVo.setMachineURI(url.get(0));
        //
        // if (!NumberUtils.isNumber(url.get(1))) {
        // logger.error("init server :" + server + " error !");
        // continue;
        // }
        //
        // int port = Integer.valueOf(url.get(1)).intValue();
        //
        // remoteServerVo.setMachinePort(port);
        //
        // serverMap.put(KeyGenerate.serverAggregateKeyGenerate(url.get(0), port), remoteServerVo);
        //
        // }

        // wsServerConfig.withServerMap(serverMap);
    }

    private void initPort(ServerConfig wsServerConfig, Properties properties) throws SocketException {
        int localClientPort = defaultClientPort;
        String clientPort = (String) properties.get(CLIENT_PORT);
        if (NumberUtils.isNumber(clientPort.trim())) {
            localClientPort = Integer.valueOf(clientPort.trim());
        }

        int localServerPort = defaultServerPort;
        String serverPort = (String) properties.get(SERVER_PORT);
        if (NumberUtils.isNumber(serverPort.trim())) {
            localServerPort = Integer.valueOf(serverPort.trim());
        }

        String realIp = NetUtils.loadRealIp();

        logger.info("local port : {} . server port: {}", clientPort, serverPort);
        wsServerConfig.withHost(realIp).withClientPort(localClientPort).withServerPort(localServerPort);
    }

    public static void main(String[] args) {
        String property = System.getProperties().getProperty("os.name");
        System.out.println(property);

    }
}
