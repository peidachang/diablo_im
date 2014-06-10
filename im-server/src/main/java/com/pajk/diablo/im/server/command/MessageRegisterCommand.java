package com.pajk.diablo.im.server.command;

import java.util.List;
import java.util.Map;

import com.pajk.diablo.im.common.store.UserMessageManager;
import com.pajk.diablo.im.server.core.RequestHelper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pajk.diablo.im.common.store.TairManagerProxy;
import com.pajk.diablo.im.common.util.JsonUtils;
import com.pajk.diablo.im.common.util.KeyGenerate;
import com.pajk.diablo.im.common.util.RequestConst;
import com.pajk.diablo.im.server.core.RegisterManager;
import com.pajk.diablo.im.server.core.SysApplicationContext;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * <pre>
 * Created by zhaoming on 14-6-3 上午9:37
 * 用户注册自己的IP及端口
 * </pre>
 */
public class MessageRegisterCommand implements MessageCommand {

    private static final Logger logger = LoggerFactory.getLogger(MessageRegisterCommand.class.getName());

    @Override
    public Object invoke(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        logger.debug("client registed : {}", ctx.channel().remoteAddress());

        String request = RequestHelper.frame2String(frame);

        Map<String, Object> objectMap = JsonUtils.fromStrToMap(request);

        String userId = (String) objectMap.get(RequestConst.USERID);

        // 将用户信息保存到本地缓存和tair中
        RegisterManager.registerUser(userId, ctx.channel());

        // 察看是否有用户未读的消息
        sendUnReadMsg(ctx, userId);

        ctx.channel().write(new TextWebSocketFrame("register success:" + userId));

        return null;
    }

    private void sendUnReadMsg(ChannelHandlerContext ctx, String userId) {

        try {

            TairManagerProxy tairManagerProxy = SysApplicationContext.getInstance().getTairManagerProxy();
            // Result<DataEntry> dataEntryResult = tairManagerProxy.get(KeyGenerate.userUnreadMsgKeyGenerate(userId));
            List<String> messageList = UserMessageManager.getUnreadMsg2Tair(tairManagerProxy, userId);

            // if (TairManagerProxy.isSuccess(dataEntryResult)) {

            // ArrayList<String> messageList = (ArrayList) dataEntryResult.getValue().getValue();
            // List<String> messageList = UserMessageManager.getUnreadMsg2Tair(tairManagerProxy, userId);
            if (CollectionUtils.isNotEmpty(messageList)) {

                for (String message : messageList) {
                    ctx.channel().write(new TextWebSocketFrame(message));
                }
                tairManagerProxy.delete(KeyGenerate.userUnreadMsgKeyGenerate(userId));
            }

            // }

        } catch (Exception e) {
            logger.error("send unread message error ! ", e);
        }
    }
}
