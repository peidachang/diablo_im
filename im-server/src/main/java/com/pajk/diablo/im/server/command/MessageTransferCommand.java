package com.pajk.diablo.im.server.command;

import java.util.Map;

import com.pajk.diablo.im.server.core.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pajk.diablo.im.common.store.UserMessageManager;
import com.pajk.diablo.im.common.util.JsonUtils;
import com.pajk.diablo.im.common.util.RequestConst;
import com.pajk.diablo.im.server.core.SysApplicationContext;
import com.pajk.diablo.im.server.monitor.SystemMonitor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * <pre>
 * Created by zhaoming on 14-5-29 下午3:40
 * 消息传递的处理类
 * </pre>
 */
public class MessageTransferCommand implements MessageCommand {

    private static final Logger logger = LoggerFactory.getLogger(MessageTransferCommand.class.getName());

    @Override
    public Object invoke(ChannelHandlerContext ctx, WebSocketFrame frame) {

        String request = RequestHelper.frame2String(frame);
        String toUserId = null;
        try {

            Map<String, Object> objectMap = JsonUtils.fromStrToMap(request);

            SysApplicationContext wsApplication = SysApplicationContext.getInstance();

            // 转发数据
            String fromUserId = (String) objectMap.get(RequestConst.FROM_USERID);
            toUserId = (String) objectMap.get(RequestConst.TO_USERID);

            String message = (String) objectMap.get(RequestConst.MSG);
            logger.debug("from userid: {} to userId:  {} .server receive message :", fromUserId, toUserId, message);

            // 非本机数据
            Channel channel = wsApplication.getClientChannel(toUserId);
            if (channel == null || !channel.isOpen()) {

                // todo 后面可以再查询一次tair 并将消息转发出去
                logger.warn("lost message notice : {}  by user {}  not login!", request, toUserId);
                SystemMonitor.getInstance().increServerTransferCallHandleFailNums();// 消息转发处理失败数
                UserMessageManager.storeUnreadMsg2Tair(wsApplication.getTairManagerProxy(), toUserId, request);

                return null;
            }

            channel.writeAndFlush(new TextWebSocketFrame("received message :" + message + " from :" + fromUserId));
            SystemMonitor.getInstance().incrServerTransferCallHandleSuccessNums();// 消息转发处理成功数

            return null;

        } catch (Exception e) {

            logger.error("error data : {}", request, e);

            // Send the uppercase string back.
            UserMessageManager.storeUnreadMsg2Tair(SysApplicationContext.getInstance().getTairManagerProxy(), toUserId,
                                                   request);
            ctx.channel().write(new TextWebSocketFrame(request.toUpperCase()));
        }
        return null;
    }
}
