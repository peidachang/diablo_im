package com.pajk.diablo.im.common.util;

import org.apache.commons.codec.binary.Base64;

/**
 * <pre>
 * Created by zhaoming on 14-6-4 上午11:44s
 * </pre>
 */
public abstract class KeyGenerate {

    private static final String serverAggregateKey        = "serverAggregateKey";

    private static final String serverAggregateMonitorKey = "serverAggregateMonitorKey";

    public static final String  CHANNEL_PREFIX            = "channel";

    public static final String  AGGREGATE_PREFIX          = "aggregate";

    public static final String  MONITOR_PREFIX            = "monitor";

    public static final String  USER_UNREAD_PREFIX        = "userUnread";

    public static final String  USER_LOCATION             = "userLocation";

    /**
     * 被动通道的key生成规则器
     * 
     * @param clientHost 客户端地址
     * @param serverHost 服务端地址
     * @return base64编码后的信息
     */
    public static String serverChannelKeyGenerate(String clientHost, String serverHost) {

        StringBuilder stringBuilder = new StringBuilder(50);
        stringBuilder.append(CHANNEL_PREFIX).append(clientHost).append(":").append(serverHost);
        return encode(stringBuilder.toString());
    }

    /**
     * 集群的key生成规则
     * 
     * @param host 主机地址
     * @param port 端口
     * @return base64编码后的信息
     */
    public static String serverAggregateKeyGenerate(String host, int port) {
        StringBuilder stringBuilder = new StringBuilder(50);
        stringBuilder.append(AGGREGATE_PREFIX).append(host).append(":").append(port);
        return encode(stringBuilder.toString());
    }

    /**
     * base64编码
     * 
     * @param value 需要编码的值
     * @return base64编码后的信息
     */
    public static String encode(String value) {
        return Base64.encodeBase64String(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(value));
    }

    /**
     * 监控数据在tair中的key
     * 
     * @param host 主机地址
     * @param port 主机端口
     * @return base64编码后的信息
     */
    public static String monitorKeyGenerate(String host, int port) {
        StringBuilder stringBuilder = new StringBuilder(50);
        stringBuilder.append(MONITOR_PREFIX).append(host).append(":").append(port);
        return encode(stringBuilder.toString());
    }

    /**
     * 获取集群在tair中的key
     * 
     * @return base64编码后的信息
     */
    public static String getServerAggregateKey4Tair() {
        return encode(serverAggregateKey);
    }

    /**
     * 监控数据的key
     * 
     * @return base64编码后的信息
     */
    public static String getServerAggregateMonitorKey() {
        return encode(serverAggregateMonitorKey);
    }

    public static String userUnreadMsgKeyGenerate(String toUserId) {
        StringBuilder stringBuilder = new StringBuilder(50);
        stringBuilder.append(USER_UNREAD_PREFIX).append(toUserId);
        return stringBuilder.toString();
    }

    public static String userLocationKeyGenerate(String userId) {
        StringBuilder stringBuilder = new StringBuilder(50);
        stringBuilder.append(USER_LOCATION).append(userId);
        return encode(stringBuilder.toString());
    }

    public static void main(String[] args) {

        String localhost = serverAggregateKeyGenerate("localhost", 1010);
        System.out.println(localhost);

        String localhost1 = serverAggregateKeyGenerate("localhost", 1010);
        System.out.println(localhost1);
    }
}
