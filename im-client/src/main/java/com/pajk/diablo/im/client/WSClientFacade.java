package com.pajk.diablo.im.client;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pajk.diablo.im.common.common.DefaultClientChannelInitializer;
import com.pajk.diablo.im.common.enums.RequestTypeEnum;
import com.pajk.diablo.im.common.store.TairManagerProxy;
import com.pajk.diablo.im.common.store.UserMessageManager;
import com.pajk.diablo.im.common.util.KeyGenerate;
import com.pajk.diablo.im.common.util.LogUtils;
import com.pajk.diablo.im.common.util.NetUtils;
import com.pajk.diablo.im.common.vo.MessageVo;
import com.pajk.diablo.im.common.vo.UserLocationVo;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.impl.DefaultTairManager;

/**
 * <pre>
 * Created by zhaoming on 14-6-3 下午1:52
 * </pre>
 */
public class WSClientFacade {

    private static final Logger                    logger            = LoggerFactory.getLogger(WSClientFacade.class.getName());

    private LoadingCache<UserLocationVo, WSClient> serverClientCache = CacheBuilder.newBuilder().concurrencyLevel(Runtime.getRuntime().availableProcessors()).build(new CacheLoader<UserLocationVo, WSClient>() {

                                                                                                                                                                        @Override
                                                                                                                                                                        public WSClient load(UserLocationVo userLocationVo)
                                                                                                                                                                                                                           throws Exception {

                                                                                                                                                                            return createClient(userLocationVo);
                                                                                                                                                                        }
                                                                                                                                                                    });

    private static WSClientFacade                  instance          = new WSClientFacade();

    private static TairManagerProxy                tairManagerProxy;

    // private volatile static boolean isInit = false;

    private WSClientFacade() {
    }

    private static Object lock = new Object();

    public static WSClientFacade getInstance() {
        return instance;
    }

    // public WSClientFacade build() throws WSClientException {
    // if (!isInit) {
    // synchronized (lock) {
    // if (!isInit) {
    // try {
    // init();
    // } catch (Exception e) {
    // throw new WSClientException("init client error!", e);
    // }
    // isInit = true;
    // }
    // }
    // }
    // }

    public static void main(String[] args) {
        LogUtils.initLogback();

        MessageVo messageVo = new MessageVo();
        messageVo.setType(RequestTypeEnum.MESSAGE_TRANSFER.getType());
        messageVo.setFromUserId("13588701563");
        messageVo.setToUserId("13588701564");
        messageVo.setMsg("hehe11");

        WSClientFacade.getInstance().withDefaultTairManager(new DefaultTairManager()).send(messageVo);

    }

    public void setDefaultTairManager(DefaultTairManager tairManager) {
        Preconditions.checkNotNull(tairManager, "tair manger is null!");
        tairManagerProxy = new TairManagerProxy(tairManager);
    }

    public WSClientFacade withDefaultTairManager(DefaultTairManager tairManager) {

        Preconditions.checkNotNull(tairManager, "tair manger is null!");
        tairManagerProxy = new TairManagerProxy(tairManager);
        return this;
    }

    // private static void init() throws Exception {
    //
    // Properties properties = PropertiesUtils.getProperties();
    //
    // tairManagerProxy = TairManagerProxy.newBuilder().withProperties(properties).build();
    //
    // }

    public void send(MessageVo messageVo) {

        Preconditions.checkNotNull(messageVo, "messagevo is null!");
        Preconditions.checkNotNull(tairManagerProxy, "未初始化tairManagerProxy");

        String jsonMessage = JSON.toJSONString(messageVo);

        // 查找用户所在机器
        Result<DataEntry> entryResult = tairManagerProxy.get(messageVo.getToUserId());
        if (!TairManagerProxy.isSuccess(entryResult)) {
            logger.warn("lost message . {}", messageVo.getToUserId());

            // 将消息存储到tair中
            UserMessageManager.storeUnreadMsg2Tair(tairManagerProxy, messageVo.getToUserId(), jsonMessage);
            return;
        }

        UserLocationVo value = (UserLocationVo) entryResult.getValue().getValue();

        try {

            WSClient wsClient = serverClientCache.get(value);
            if (!wsClient.isOpen()) {
                synchronized (lock) {
                    wsClient = createClient(value);
                    serverClientCache.put(value, wsClient);
                }
            }

            wsClient.sendMsg(jsonMessage);

        } catch (Exception e) {

            // 将消息存储到tair中
            UserMessageManager.storeUnreadMsg2Tair(tairManagerProxy, messageVo.getToUserId(), jsonMessage);
        }
    }

    private WSClient createClient(UserLocationVo userLocationVo) throws Exception {

        URI uri = NetUtils.configWSURI(userLocationVo.getUri());

        // AbstractClientHandler handler = new WSDefaultClientHandler(
        // WebSocketClientHandshakerFactory.newHandshaker(uri,
        // WebSocketVersion.V13,
        // null,
        // false,
        // new DefaultHttpHeaders()));
        //
        // ChannelInitializer wsChannelInitializer = ChannelInitializerFactory.getWSChannelInitializer(uri, handler);

        // WSClient client =
        // WSClient.newBuilder(uri).withWSClientHandler(handler).withChannelInitializer(wsChannelInitializer);

        DefaultClientChannelInitializer wsChannelInitializer = new DefaultClientChannelInitializer(uri);

        WSClient client = WSClient.newBuilder(uri).withChannelInitializer(wsChannelInitializer);

        client.establish();

        return client;
    }
}
