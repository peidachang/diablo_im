package com.pajk.diablo.im.server.factory;

import java.net.URI;

import com.pajk.diablo.im.client.WSClient;
import com.pajk.diablo.im.common.enums.RequestTypeEnum;
import com.pajk.diablo.im.server.core.DefaultServerChannelInitializer;

/**
 * <pre>
 * Created by zhaoming on 14-5-28 下午4:49
 * </pre>
 */
public class WSClientFactory {

    /**
     * 创建一个简单的WS客户端，不处理回传的数据
     * 
     * @param uri 建立连接的地址
     * @param serverName 服务名称
     * @return
     */
    public static WSClient newSimpleClient(URI uri, String serverName, RequestTypeEnum requestTypeEnum) {

        // 只发送数据，不接受数据
        // final AbstractClientHandler handler = new ServerClientHandler(
        // WebSocketClientHandshakerFactory.newHandshaker(uri,
        // WebSocketVersion.V13,
        // null,
        // false,
        // new DefaultHttpHeaders()),
        // MessageCammandFactory.getCommand(requestTypeEnum));
        //
        // ChannelInitializer wsChannelInitializer = ChannelInitializerFactory.getWSChannelInitializer(uri, handler);

        DefaultServerChannelInitializer wsChannelInitializer = new DefaultServerChannelInitializer(uri, requestTypeEnum);

        // DefaultClientInitializer wsChannelInitializer = new DefaultClientInitializer(uri);

        // return
        // return
        // WSClient.newBuilder(uri).withName(serverName).withWSClientHandler(handler).withChannelInitializer(wsChannelInitializer);
        return WSClient.newBuilder(uri).withName(serverName).withChannelInitializer(wsChannelInitializer);
    }

}
