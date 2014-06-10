package com.pajk.diablo.im.server.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.pajk.diablo.im.server.vo.RemoteServerVo;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 下午3:27
 * </pre>
 */
public class ServerConfig {

    private int                         localMachineClientPort;                           // 同客户端通信的端口
    private int                         localMachineServerPort;                           // 同服务端通信的端口
    private String                      localMachineHost;

    // 定时清理的task延迟时间
    private long                        monitorPeriod;
    private TimeUnit                    monitorTimeunit;

    private Map<String, RemoteServerVo> serverMap = new HashMap<String, RemoteServerVo>();

    public static ServerConfig newBuilder() {
        return new ServerConfig();
    }

    public int getLocalMachineClientPort() {
        return localMachineClientPort;
    }

    public ServerConfig withClientPort(int port) {
        this.localMachineClientPort = port;
        return this;
    }

    public String getLocalMachineHost() {
        return localMachineHost;
    }

    public ServerConfig withHost(String host) {
        this.localMachineHost = host;
        return this;
    }

    public TimeUnit getMonitorTimeunit() {
        return monitorTimeunit;
    }

    public ServerConfig withMonitorTimeunit(TimeUnit monitorTimeunit) {
        this.monitorTimeunit = monitorTimeunit;
        return this;
    }

    public Map<String, RemoteServerVo> getServerMap() {
        return serverMap;
    }

    public ServerConfig withServerMap(Map<String, RemoteServerVo> serverMap) {
        this.serverMap = serverMap;
        return this;
    }

    public long getMonitorPeriod() {
        return monitorPeriod;
    }

    public ServerConfig withMonitorPeriod(long monitorPeriod) {
        this.monitorPeriod = monitorPeriod;
        return this;
    }

    public int getLocalMachineServerPort() {
        return localMachineServerPort;
    }

    public ServerConfig withServerPort(int localMachineServerPort) {
        this.localMachineServerPort = localMachineServerPort;
        return this;
    }
}
