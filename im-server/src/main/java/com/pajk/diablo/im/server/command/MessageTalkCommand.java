package com.pajk.diablo.im.server.command;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pajk.diablo.im.client.WSClient;
import com.pajk.diablo.im.common.enums.RequestTypeEnum;
import com.pajk.diablo.im.common.store.TairManagerProxy;
import com.pajk.diablo.im.common.store.UserMessageManager;
import com.pajk.diablo.im.common.util.JsonUtils;
import com.pajk.diablo.im.common.util.KeyGenerate;
import com.pajk.diablo.im.common.util.RequestConst;
import com.pajk.diablo.im.common.vo.UserLocationVo;
import com.pajk.diablo.im.server.core.RegisterManager;
import com.pajk.diablo.im.server.core.RequestHelper;
import com.pajk.diablo.im.server.core.SysApplicationContext;
import com.pajk.diablo.im.server.factory.WSClientFactory;
import com.pajk.diablo.im.server.monitor.SystemMonitor;
import com.pajk.diablo.im.server.vo.RemoteServerVo;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * <pre>
 * Created by zhaoming on 14-6-3 上午10:07
 * 对话数据处理
 * </pre>
 */
public class MessageTalkCommand implements MessageCommand {

    private static final Logger logger = LoggerFactory.getLogger(MessageTalkCommand.class.getName());

    @Override
    public Object invoke(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        String request = RequestHelper.frame2String(frame);
        Map<String, Object> objectMap = JsonUtils.fromStrToMap(request);

        // 单个对话数据处理
        singleTalk(request, objectMap);

        return null;
    }

    public void singleTalk(String request, Map<String, Object> objectMap) throws Exception {

        String fromUserId = (String) objectMap.get(RequestConst.FROM_USERID);
        String toUserId = (String) objectMap.get(RequestConst.TO_USERID);
        String message = (String) objectMap.get(RequestConst.MSG);

        SysApplicationContext wsApplication = SysApplicationContext.getInstance();
        logger.debug("from userid:  to userId:   .server receive message :", fromUserId, toUserId, message);

        SystemMonitor.getInstance().increClientCallTotalNums();// 总的调用次数

        UserLocationVo userLocation = RegisterManager.loadUserLocation(toUserId);
        if (userLocation == null || userLocation.isNull()) {

            // 本机消息处理失败数,缓存到tair中
            logger.warn("lost message notice :  {}  by user {}  not login!", request, toUserId);
            SystemMonitor.getInstance().increClientCallFailNums();
            UserMessageManager.storeUnreadMsg2Tair(wsApplication.getTairManagerProxy(), toUserId, request);
            return;
        }

        // 若是传输的数据对象在本地的话，直接去拿相应的通道，传输数据
        if (RequestHelper.isInSameServer(userLocation)) {

            Channel toChannel = wsApplication.getClientChannel(toUserId);
            if (toChannel == null || !toChannel.isOpen() || !toChannel.isActive()) {

                // todo 后面可以再查询一次tair 并将消息转发出去 -- 本机消息处理失败数,缓存到tair中
                logger.warn("lost message notice :  {}  by user {}  not login!", request, toUserId);
                SystemMonitor.getInstance().increClientCallFailNums();

                UserMessageManager.storeUnreadMsg2Tair(wsApplication.getTairManagerProxy(), toUserId, request);
                return;
            }

            SystemMonitor.getInstance().increClientCallSuccessNums();// 本机消息处理成功数
            toChannel.writeAndFlush(new TextWebSocketFrame("has new message :" + message + " from :" + fromUserId));
            return;
        }

        // objectMap.put(RequestConst.PRARAM_TYPE, RequestTypeEnum.MESSAGE_TRANSFER.getType());
        // String transforMessage = JsonUtils.toStr(objectMap);

        // 非本节点数据
        String transforMessage = configTransferMessage(objectMap);

        Channel serverChannel = wsApplication.getServerChannel(KeyGenerate.serverChannelKeyGenerate(wsApplication.getServerConfig().getLocalMachineHost(),
                                                                                                    userLocation.getMachineHost()));

        SystemMonitor.getInstance().increServerTransferCallTotalNums();// 消息转发总数

        // 已经被动的建立连接的话
        if (serverChannel != null && serverChannel.isOpen()) {
            serverChannel.writeAndFlush(new TextWebSocketFrame(transforMessage));
            SystemMonitor.getInstance().increServerTransferCallSuccessNums();// 消息转发成功数
            return;
        }

        // 建立主动的连接
        WSClient wsClient = wsApplication.getServerWSClient(userLocation.getMachineHost());
        if (wsClient == null || !wsClient.isOpen()) {

            // 配置的服务端通信地址及端口
            Result<DataEntry> result = wsApplication.getTairManagerProxy().get(KeyGenerate.getServerAggregateKey4Tair());
            if (TairManagerProxy.isSuccess(result)) {
                HashMap<String, RemoteServerVo> serverMap = (HashMap<String, RemoteServerVo>) result.getValue().getValue();

                RemoteServerVo remoteServerVo = serverMap.get(KeyGenerate.serverAggregateKeyGenerate(userLocation.getMachineHost(),
                                                                                                     userLocation.getMachinePort()));

                // 简单校验是否存在地址
                if (remoteServerVo == null) {
                    logger.warn("invalid client : {} ", userLocation.toString());
                    SystemMonitor.getInstance().increServerTransferCallFailNums();// 消息转发失败数

                    UserMessageManager.storeUnreadMsg2Tair(wsApplication.getTairManagerProxy(), toUserId,
                                                           transforMessage);
                    return;
                }

            } else {
                logger.warn("ignore server check : {}", userLocation.toString());
            }

            // logger.debug("remote server info:" + remoteServerVo.toString());

            logger.info("create new channel from {} to {} ", wsApplication.getServerConfig().getLocalMachineHost(),
                        userLocation.getMachineHost());

            wsClient = getWsClient(userLocation);
            wsClient.establish();

            SysApplicationContext.getInstance().addServerWSClient(userLocation.getMachineHost(), wsClient);

        }

        wsClient.sendMsg(transforMessage);
        SystemMonitor.getInstance().increServerTransferCallSuccessNums();
    }

    protected String configTransferMessage(Map<String, Object> objectMap) throws IOException {
        objectMap.put(RequestConst.PRARAM_TYPE, RequestTypeEnum.MESSAGE_TRANSFER.getType());
        return JsonUtils.toStr(objectMap);
    }

    private WSClient getWsClient(UserLocationVo userLocation) throws URISyntaxException {
        WSClient wsClient;
        StringBuilder urlBuilder = new StringBuilder(100);// example ws://localhost:8080/websocket
        urlBuilder.append("ws://").append(userLocation.getMachineHost()).append(":").append(userLocation.getMachinePort()).append(RequestConst.WEB_SOCKET_CONTENT_PATH);

        URI uri = new URI(urlBuilder.toString());

        StringBuilder serverNameBuilder = new StringBuilder(200);
        String serverName = serverNameBuilder.append("server-2-server:").append(SysApplicationContext.getInstance().getServerConfig().getLocalMachineHost()).append(":").append(SysApplicationContext.getInstance().getServerConfig().getLocalMachineServerPort()).append(" to ").append(userLocation.getUri()).toString();

        wsClient = WSClientFactory.newSimpleClient(uri, serverName, RequestTypeEnum.MESSAGE_TRANSFER);
        return wsClient;
    }
}
