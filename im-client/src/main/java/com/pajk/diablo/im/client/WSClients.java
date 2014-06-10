package com.pajk.diablo.im.client;

/**
 * <pre>
 * Created by zhaoming on 14-6-3 下午3:59
 * </pre>
 */
public class WSClients {

    /**
     * 默认空实现
     */
    public static WSClients client = new NullWSClient();

    public static class NullWSClient extends WSClients {

        public boolean isOpen() {
            return false;
        }
    }

}
