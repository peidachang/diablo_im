package com.pajk.diablo.im.server.handler;

import java.net.SocketAddress;

import com.pajk.diablo.im.common.util.KeyGenerate;
import com.pajk.diablo.im.server.core.RequestHelper;
import com.pajk.diablo.im.server.core.SysApplicationContext;
import io.netty.channel.ChannelHandlerContext;

/**
 * <pre>
 * Created by zhaoming on 14-6-3 上午10:33
 * 
 * </pre>
 */
public class Server4ServerHandler extends DefaultServerHandler {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (null == remoteAddress) {
            logger.error("can not find user ip and port : {}", remoteAddress);
        }

        logger.info("client : {}  connected to server ! ", remoteAddress);

        String remoteHost = RequestHelper.getRemoteHost(ctx.channel().remoteAddress().toString());
        String localHost = SysApplicationContext.getInstance().getServerConfig().getLocalMachineHost();

        SysApplicationContext.getInstance().addServerChannel(KeyGenerate.serverChannelKeyGenerate(remoteHost, localHost),
                                                             ctx.channel());

        super.channelRegistered(ctx);
    }

}
