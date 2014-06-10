package com.pajk.diablo.im.server.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.pajk.diablo.im.server.core.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pajk.diablo.im.common.enums.RequestTypeEnum;
import com.pajk.diablo.im.common.store.TairManagerProxy;
import com.pajk.diablo.im.common.util.JsonUtils;
import com.pajk.diablo.im.common.util.RequestConst;
import com.pajk.diablo.im.server.core.SysApplicationContext;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * <pre>
 * Created by zhaoming on 14-6-3 上午10:07
 * 劝阻对话的数据处理
 * </pre>
 */
public class MessageGroupTalkCommand extends MessageTalkCommand {

    private static final Logger logger = LoggerFactory.getLogger(MessageGroupTalkCommand.class.getName());

    @Override
    public Object invoke(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        // String request = ((TextWebSocketFrame) frame).text();
        String request = RequestHelper.frame2String(frame);
        Map<String, Object> objectMap = JsonUtils.fromStrToMap(request);

        String fromUserId = (String) objectMap.get(RequestConst.FROM_USERID);
        String message = (String) objectMap.get(RequestConst.MSG);
        String groupId = (String) objectMap.get(RequestConst.GROUP_ID);

        TairManagerProxy tairManagerProxy = SysApplicationContext.getInstance().getTairManagerProxy();

        Result<DataEntry> dataEntryResult = tairManagerProxy.get(groupId);

        if (!TairManagerProxy.isSuccess(dataEntryResult)) {
            logger.warn("ignore group message. groupID:{} ", groupId);
            return null;
        }

        try {
            ArrayList<String> userIdList = (ArrayList<String>) dataEntryResult.getValue().getValue();

            logger.info("receive group message ,groupid: {} ids: {}", groupId, userIdList.toString());

            for (String toUserId : userIdList) {

                Map<String, Object> messageMap = configRequestMap(fromUserId, message, toUserId);
                String requestTemp = JsonUtils.toStr(messageMap);

                // 对话数据处理
                singleTalk(requestTemp, messageMap);
            }

        } catch (Exception e) {
            logger.error("load group info error!", e);
        }

        return null;
    }

    private Map<String, Object> configRequestMap(String fromUserId, String message, String toUserId) {
        Map<String, Object> messageMap = new HashMap<String, Object>(4);
        messageMap.put(RequestConst.PRARAM_TYPE, RequestTypeEnum.TALK.getType());
        messageMap.put(RequestConst.FROM_USERID, fromUserId);
        messageMap.put(RequestConst.TO_USERID, toUserId);
        messageMap.put(RequestConst.MSG, message);
        return messageMap;
    }
}
