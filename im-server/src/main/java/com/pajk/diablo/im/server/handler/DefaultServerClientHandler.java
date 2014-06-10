package com.pajk.diablo.im.server.handler;

import com.google.common.base.Preconditions;
import com.pajk.diablo.im.common.common.AbstractClientHandler;
import com.pajk.diablo.im.server.command.MessageCommand;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * <pre>
 * Created by zhaoming on 14-5-28 下午4:25
 * 对于服务端过来的数据根据不同的请求类型进行处理
 * </pre>
 */
public class DefaultServerClientHandler extends AbstractClientHandler {

    // private static final Logger logger = LoggerFactory.getLogger(ServerClientHandler.class.getName());

    private MessageCommand messageCommand;

    public DefaultServerClientHandler(WebSocketClientHandshaker handshaker, MessageCommand messageCommand) {
        super(handshaker);
        this.messageCommand = messageCommand;
    }

    @Override
    public void resultHandle(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        Preconditions.checkNotNull(ctx, "ctx is null!");
        Preconditions.checkNotNull(frame, "frame is null!");

        // logger.info("handle class :" + messageCommand.getClass().getSimpleName());

        if (frame instanceof TextWebSocketFrame) {
            messageCommand.invoke(ctx, frame);
        }

    }
}
