package com.pajk.diablo.im.server.command;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * <pre>
 * Created by zhaoming on 14-5-29 下午2:05
 * 消息处理的接口
 * </pre>
 */
public interface MessageCommand<T> {

    /**
     * 消息的处理
     * 
     *
     *
     * @param ctx 通道的上下文
     * @param frame 传输的websocket数据
     * @return
     * @throws Exception
     */
    T invoke(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception;
}
