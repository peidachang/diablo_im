package com.pajk.diablo.im.common.common;

import javax.net.ssl.SSLContext;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 上午11:44
 * </pre>
 */
public class WebSocketSslClientContextFactory {

    private static final String     PROTOCOL = "TLS";
    private static final SSLContext CONTEXT;

    static {
        SSLContext clientContext;
        try {
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, WebSocketSslClientTrustManagerFactory.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }

        CONTEXT = clientContext;
    }

    public static SSLContext getContext() {
        return CONTEXT;
    }

    private WebSocketSslClientContextFactory() {
        // Unused
    }
}
