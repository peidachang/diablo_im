package com.pajk.diablo.im.server.handler;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.Map;

import com.pajk.diablo.im.server.core.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pajk.diablo.im.common.enums.RequestTypeEnum;
import com.pajk.diablo.im.common.util.JsonUtils;
import com.pajk.diablo.im.common.util.RequestConst;
import com.pajk.diablo.im.server.command.MessageCammandFactory;
import com.pajk.diablo.im.server.command.MessageCommand;
import com.pajk.diablo.im.server.monitor.MonitorPage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 上午9:59
 * </pre>
 */
public class DefaultServerHandler extends SimpleChannelInboundHandler<Object> {

    protected static final Logger     logger = LoggerFactory.getLogger(DefaultServerHandler.class.getName());

    // ws的握手
    private WebSocketServerHandshaker handshaker;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (null == remoteAddress) {
            logger.error("can not find user ip and port ! {}", remoteAddress);
        }

        logger.warn("client :  {} connected to server !", remoteAddress);

        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // Handle a bad request.
        if (!req.getDecoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        // Allow only GET methods.
        if (req.getMethod() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        if ("/monitor".equals(req.getUri())) {
            ByteBuf content = MonitorPage.getMonitorPage();
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

            res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
            setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
            return;
        }

        // http请求处理
        httpHandle(ctx);

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req),
                                                                                          null, false);
        logger.info("client handshake: {}", ctx.channel().remoteAddress());

        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            try {
                handshaker.handshake(ctx.channel(), req);
            } catch (Exception e) {
                logger.info("client close connection. client ip :  {}  request : {}", ctx.channel().remoteAddress(),
                            req);
            }
        }
    }

    protected void httpHandle(ChannelHandlerContext ctx) {
        // default handle
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        String inputMessage = RequestHelper.frame2String(frame);

        // 请求处理handle

        try {

            Map<String, Object> objectMap = JsonUtils.fromStrToMap(inputMessage);
            int type = ((Integer) objectMap.get(RequestConst.PRARAM_TYPE)).intValue();

            RequestTypeEnum requestTypeEnum = RequestTypeEnum.getRequestTypeEnum(type);

            MessageCommand command = MessageCammandFactory.getCommand(requestTypeEnum);

            command.invoke(ctx, frame);

        } catch (Exception e) {

            logger.error("error data : {}", inputMessage, e);

            // Send the uppercase string back.
            ctx.channel().write(new TextWebSocketFrame(inputMessage.toUpperCase()));
            return;
        }

    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        return "ws://" + req.headers().get(HOST) + RequestConst.WEB_SOCKET_CONTENT_PATH;
    }
}
