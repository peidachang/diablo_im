package com.pajk.diablo.im.server.core;

import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.pajk.diablo.im.client.WSClient;
import com.pajk.diablo.im.common.store.TairManagerProxy;
import io.netty.channel.Channel;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 下午3:28
 * </pre>
 */
public class SysApplicationContext {

    private static SysApplicationContext instance           = new SysApplicationContext();

    private ServerConfig                 wsServerConfig;

    private TairManagerProxy             tairManagerProxy;

    // 服务端和客户端之间的通道缓存
    private Cache<String, Channel>       clientChannelCache = CacheBuilder.newBuilder().concurrencyLevel(Runtime.getRuntime().availableProcessors()).build();

    // 服务端之间的主动的通道缓存
    private Cache<String, WSClient>      serverClientCache  = CacheBuilder.newBuilder().concurrencyLevel(Runtime.getRuntime().availableProcessors()).build();

    // 服务端之间的主动的通道缓存
    private Cache<String, Channel>       serverChannelCache = CacheBuilder.newBuilder().concurrencyLevel(Runtime.getRuntime().availableProcessors()).build();

    private SysApplicationContext() {
    }

    public void withTairManagerProxy(TairManagerProxy proxy) {
        this.tairManagerProxy = proxy;
    }

    public TairManagerProxy getTairManagerProxy() {
        return tairManagerProxy;
    }

    public static SysApplicationContext getInstance() {
        return instance;
    }

    public ServerConfig getServerConfig() {

        Preconditions.checkNotNull(wsServerConfig, "wsServerConfig is null!");
        return wsServerConfig;
    }

    public void setWsServerConfig(ServerConfig wsServerConfig) {
        this.wsServerConfig = wsServerConfig;
    }

    /**
     * 缓存已经注册的channel
     * 
     * @param userId 用户ID
     * @param channel channel对象
     */
    public void addChannel(String userId, Channel channel) {

        Preconditions.checkNotNull(userId, "userId is null!");
        Preconditions.checkNotNull(channel, "channel is null!");

        clientChannelCache.put(userId, channel);
    }

    /**
     * 根据用户ID获取channel对象
     * 
     * @param userId 用户 ID
     * @return
     */
    public Channel getClientChannel(String userId) {
        Preconditions.checkNotNull(userId, "userId is null!");

        return clientChannelCache.getIfPresent(userId);
    }

    /**
     * 删除客户端同服务端之间的通信
     * 
     * @param userId
     */
    public void removeClientChannel(String userId) {
        clientChannelCache.invalidate(userId);
        // todo 更新tair中数据
    }

    /**
     * 删除服务端之间的通道 -- 服务端
     * 
     * @param key
     */
    public void removeServerClient(String key) {
        serverClientCache.invalidate(key);
    }

    /**
     * 删除服务端之间的通道 -- 客户端
     * 
     * @param key
     */
    public void removeServerChannel(String key) {
        serverChannelCache.invalidate(key);
    }

    /**
     * 获取所有的客户端链接
     * 
     * @return
     */
    public HashMap<String, Channel> loadAllClientChannel() {

        Preconditions.checkNotNull(clientChannelCache, "clientChannelCache is null!");

        ConcurrentMap<String, Channel> channelConcurrentMap = clientChannelCache.asMap();

        return new HashMap(channelConcurrentMap);
    }

    /**
     * 获取所有的客户端链接
     * 
     * @return
     */
    public HashMap<String, WSClient> loadServerClientChannel() {

        Preconditions.checkNotNull(serverClientCache, "serverClientCache is null!");

        ConcurrentMap<String, WSClient> channelConcurrentMap = serverClientCache.asMap();

        return new HashMap(channelConcurrentMap);
    }

    public HashMap<String, Channel> loadServerChannelChannel() {

        Preconditions.checkNotNull(serverChannelCache, "serverClientCache is null!");

        ConcurrentMap<String, Channel> channelConcurrentMap = serverChannelCache.asMap();

        return new HashMap(channelConcurrentMap);
    }

    /**
     * 获取指定客户端的终端连接
     * 
     * @param machineIP
     * @return
     */
    public WSClient getServerWSClient(String machineIP) {
        return serverClientCache.getIfPresent(machineIP);
    }

    /**
     * 新增服务端之间通信的通道
     * 
     * @param machineIP 服务端的IP
     * @param wsClient 通道连接
     */
    public void addServerWSClient(String machineIP, WSClient wsClient) {

        serverClientCache.invalidate(machineIP);
        serverClientCache.put(machineIP, wsClient);
    }

    /**
     * 获取被动的连接通道
     * 
     * @param key
     * @return
     */
    public Channel getServerChannel(String key) {
        return serverChannelCache.getIfPresent(key);
    }

    /**
     * 添加被动的连接通道
     * 
     * @param key
     * @param channel
     */
    public void addServerChannel(String key, Channel channel) {

        serverChannelCache.invalidate(key);
        serverChannelCache.put(key, channel);
    }

}
