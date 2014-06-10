package com.pajk.diablo.im.server.core;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.pajk.diablo.im.common.vo.UserLocationVo;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 下午1:41
 * </pre>
 */
public class RequestHelper {

    /**
     * 判断是否本机的服务链接
     * 
     * @param userLocation 接收用户的server地址
     * @return
     */
    public static boolean isInSameServer(UserLocationVo userLocation) {
        Preconditions.checkNotNull(userLocation, "userLocation is null!");

        return userLocation.getMachineHost().equals(SysApplicationContext.getInstance().getServerConfig().getLocalMachineHost())
               && userLocation.getMachinePort() == SysApplicationContext.getInstance().getServerConfig().getLocalMachineServerPort();
    }

    /**
     * 根据URI 获取HOST
     * 
     * @param remoteAddress 远程地址
     * @return
     */
    public static String getRemoteHost(String remoteAddress) {
        if (StringUtils.isBlank(remoteAddress)) {
            return null;
        }

        String result = null;
        if (remoteAddress.startsWith("/")) {
            result = remoteAddress.substring(1);
        }

        if (result.contains(":")) {
            List<String> strings = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(result);
            result = strings.get(0);
        }

        return result;

    }

    /**
     * 将WebSocketFrame转化为String
     * 
     * @param frame 输入的请求
     * @return
     */
    public static String frame2String(WebSocketFrame frame) {

        String result = null;
        if (frame instanceof TextWebSocketFrame) {
            result = ((TextWebSocketFrame) frame).text();
        } else if (frame instanceof BinaryWebSocketFrame) {

            ByteBuf content = frame.content();
            result = content.toString(CharsetUtil.UTF_8);
        }

        return result;

    }

}
