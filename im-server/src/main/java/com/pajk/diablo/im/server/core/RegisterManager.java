package com.pajk.diablo.im.server.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.pajk.diablo.im.common.util.KeyGenerate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.pajk.diablo.im.common.store.TairManagerProxy;
import com.pajk.diablo.im.common.vo.UserLocationVo;
import com.pajk.diablo.im.server.vo.NullUserLocationVo;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import io.netty.channel.Channel;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 上午10:29
 * 服务内部的通信者
 * </pre>
 */
public class RegisterManager {

    private static final Logger           logger           = LoggerFactory.getLogger(RegisterManager.class.getName());

    // 用户链接的服务器及端口的本地缓存
    // private static Cache<Object, UserLocationVo> userIPLocalCache =
    // CacheBuilder.newBuilder().concurrencyLevel(Runtime.getRuntime().availableProcessors()).build();

    private static final TairManagerProxy tairManagerProxy = SysApplicationContext.getInstance().getTairManagerProxy();

    public static UserLocationVo loadUserLocation(final String userId) {

        Preconditions.checkNotNull(userId, "userid is null!");

        // UserLocationVo userLocation = null;

        Result<DataEntry> result = tairManagerProxy.get(KeyGenerate.userLocationKeyGenerate(userId));
        return TairManagerProxy.isSuccess(result) ? (UserLocationVo) result.getValue().getValue() : new NullUserLocationVo();

        // try {
        // userLocation = userIPLocalCache.get(userId, new Callable<UserLocationVo>() {
        //
        // public UserLocationVo call() {
        //
        // // load from remote tair
        // Result<DataEntry> result = tairManagerProxy.get(KeyGenerate.userLocationKeyGenerate(userId));
        // return TairManagerProxy.isSuccess(result) ? (UserLocationVo) result.getValue().getValue() : new
        // NullUserLocationVo();
        // }
        // });
        // } catch (ExecutionException e) {
        // logger.error("load user location error!", e);
        // }

        // Preconditions.checkNotNull(userLocation, "userlocation should not be null!");

        // return userLocation;
    }

    /**
     * 清除注册的用户
     * 
     * @param userId 用户ID
     */
    public static void unRegisterUser(String userId) {

        Preconditions.checkNotNull(userId, "userid is null!");

        logger.debug("unregister userId : {}", userId);

        // 1.清楚本地缓存
        // userIPLocalCache.invalidate(userId);

        // 2.删除tair中数据
        Result<DataEntry> dataEntryResult = tairManagerProxy.get(userId);
        if (TairManagerProxy.isSuccess(dataEntryResult)) {
            UserLocationVo value = (UserLocationVo) dataEntryResult.getValue().getValue();
            if (value.getMachineHost().equals(SysApplicationContext.getInstance().getServerConfig().getLocalMachineHost())) {
                tairManagerProxy.delete(userId);
            }
        }

        // tairManagerProxy.put(userId, new UserLocationVo(userId, ip, port));

        // 3.删除保存的通道
        SysApplicationContext.getInstance().removeClientChannel(userId);

    }

    /**
     * 将客户端用户注册到本机上
     * 
     * @param userId 用户ID
     * @param channel 通道
     */
    public static void registerUser(String userId, Channel channel) {

        ServerConfig serverConfig = SysApplicationContext.getInstance().getServerConfig();

        logger.debug("register userid: {} ip and port: {} : {}", userId, serverConfig.getLocalMachineHost(),
                     String.valueOf(serverConfig.getLocalMachineServerPort()));

        // 1.本地缓存
        // userIPLocalCache.put(userId,
        // new UserLocationVo(userId, serverConfig.getLocalMachineHost(),
        // serverConfig.getLocalMachineServerPort()));

        // 2.更新tair中数据
        tairManagerProxy.put(KeyGenerate.userLocationKeyGenerate(userId),
                             new UserLocationVo(userId, serverConfig.getLocalMachineHost(),
                                                serverConfig.getLocalMachineServerPort()));

        // 3.保存通道信息
        SysApplicationContext.getInstance().addChannel(userId, channel);

    }
}
