package com.pajk.diablo.im.common.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

/**
 * <pre>
 * Created by zhaoming on 14-5-28 下午4:12
 * </pre>
 */
public class DefaultClientHandler extends AbstractClientHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClientHandler.class.getName());

    public DefaultClientHandler(WebSocketClientHandshaker handshaker) {
        super(handshaker);
    }

    @Override
    public void resultHandle(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            logger.info("WebSocket Client received message: {}", textFrame.text());
        } else if (frame instanceof PongWebSocketFrame) {
            logger.info("WebSocket Client received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            logger.info("WebSocket Client received closing");
            ctx.channel().close();
        }
    }
}
