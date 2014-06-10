package com.pajk.diablo.im.common.util;

import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 下午3:38
 * </pre>
 */
public abstract class NetUtils {

    /**
     * 获取机器的真实IP
     * 
     * @return
     * @throws java.net.SocketException
     */
    public static String loadRealIp() throws SocketException {

        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP

        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;

        boolean finded = false;// 是否找到外网IP
        while (netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                    netip = ip.getHostAddress();
                    finded = true;

                    break;

                } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                    localip = ip.getHostAddress();
                }
            }
        }

        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localip;
        }
    }

    /**
     * 加载指定协议的默认端口
     * 
     * @param uri 要求判断的URI
     * @return
     */
    public static int loadDefaultPort(URI uri) {
        String scheme = uri.getScheme();
        int port = 80;
        if (scheme.equals("ws")) {
            port = 80;
        } else if (scheme.equals("wws")) {
            port = 443;
        }

        return port;
    }

    public static void main(String[] args) throws SocketException, ExecutionException {
        // String realIp = NetUtils.loadRealIp();
        // System.out.println(realIp);

        LoadingCache<String, String> build = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {

            @Override
            public String load(String key) throws Exception {

                if (key.equals("hello")) {
                    return "hello world!";
                } else if (key.equals("hi")) {
                    return "hi world!";
                }
                return "heihei world!";
            }
        });

        String key = "hello";
        String hello = build.get(key);
        System.out.println(hello);
        build.put("hello", "new hello!");
        String hellonew = build.get(key);
        System.out.println(hellonew);

        build.invalidate(key);
        String lastHello = build.get(key);
        System.out.println(lastHello);

    }

    /**
     * 组装WS，URI
     * 
     * @param uri
     * @return
     * @throws URISyntaxException
     */
    public static URI configWSURI(String uri) throws URISyntaxException {
        StringBuilder urlBuilder = new StringBuilder(100);
        urlBuilder.append("ws://").append(uri).append("/websocket");

        return new URI(urlBuilder.toString());
    }
}
