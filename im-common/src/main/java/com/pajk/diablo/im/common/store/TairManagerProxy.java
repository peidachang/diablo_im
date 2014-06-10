package com.pajk.diablo.im.common.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.pajk.diablo.im.common.util.PropertiesUtils;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.impl.DefaultTairManager;

/**
 * <pre>
 * Created by zhaoming on 14-5-30 上午11:13
 * </pre>
 */
public class TairManagerProxy {

    private static final Logger logger      = LoggerFactory.getLogger(TairManagerProxy.class.getName());

    private static final String TAIR_SERVER = "tair.server";
    private static final String TAIR_GROUP  = "tair.group";
    private static final String TAIR_NS     = "tair.ns";

    private DefaultTairManager  tairManager;

    private Properties          properties;

    private int                 tairNs;
    private String              tairServer;
    private String              tairGroup;

    public static final int     ONE_DAY     = 60 * 60 * 24;

    public TairManagerProxy(DefaultTairManager tairManager) {
        this.tairManager = tairManager;
    }

    public TairManagerProxy() {
    }

    public static TairManagerProxy newBuilder() {
        return new TairManagerProxy();
    }

    public int getTairNs() {
        return tairNs;
    }

    public TairManagerProxy withTairNs(int tairNs) {
        this.tairNs = tairNs;
        return this;
    }

    public String getTairServer() {
        return tairServer;
    }

    public TairManagerProxy withTairServer(String tairServer) {
        this.tairServer = tairServer;
        return this;
    }

    public String getTairGroup() {
        return tairGroup;
    }

    public TairManagerProxy withTairGroup(String tairGroup) {
        this.tairGroup = tairGroup;
        return this;
    }

    public Properties getProperties() {
        return properties;
    }

    public TairManagerProxy withProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public TairManagerProxy build() throws Exception {
        init();
        return this;

    }

    public void init() throws Exception {

        if (properties == null) {
            properties = PropertiesUtils.getProperties();
        }

        String tairServer = properties.getProperty(TAIR_SERVER);
        String tairGroup = properties.getProperty(TAIR_GROUP);
        String tairNsString = properties.getProperty(TAIR_NS);

        Preconditions.checkArgument(NumberUtils.isNumber(tairNsString), "无效的ns：" + tairNs);

        tairNs = Integer.valueOf(tairNsString).intValue();
        tairManager = new DefaultTairManager();

        List<String> confServers = new ArrayList<String>();
        confServers.add(tairServer);
        tairManager.setConfigServerList(confServers);

        tairManager.setGroupName(tairGroup);

        tairManager.init();

    }

    public ResultCode delete(Serializable key) {
        Preconditions.checkNotNull(tairManager, "please init tairManager first !");
        return tairManager.delete(tairNs, key);
    }

    public ResultCode put(Serializable key, Serializable value) {
        Preconditions.checkNotNull(tairManager, "please init tairManager first !");
        return tairManager.put(tairNs, key, value);
    }

    public ResultCode put(Serializable key, Serializable value, int version) {
        Preconditions.checkNotNull(tairManager, "please init tairManager first !");

        return tairManager.put(tairNs, key, value, version);
    }

    public ResultCode put(Serializable key, Serializable value, int version, int expireTime) {
        Preconditions.checkNotNull(tairManager, "please init tairManager first !");

        return tairManager.put(tairNs, key, value, version);
    }

    public Result<DataEntry> get(Serializable key) {
        Preconditions.checkNotNull(tairManager, "please init tairManager first !");

        return tairManager.get(tairNs, key);
    }

    public void close() {
        if (tairManager != null) {
            tairManager.close();
        }
    }

    /**
     * 并发的添加到缓存中,缓存的对象是arraylist,添加失败的话，仅仅打日志,不抛出异常
     * 
     * @param key 缓存的key
     * @param message 缓存的消息
     */
    public <T extends Serializable> void concurrentArrayListPutSilence(String key, T message, int expireTime) {

        int version = 1;
        while (true) {

            Result<DataEntry> dataEntryResult = get(key);
            if (dataEntryResult.isSuccess()) {
                ArrayList<T> valueList = null;
                if (dataEntryResult.getValue() == null || dataEntryResult.getValue().getValue() == null) {
                    valueList = new ArrayList<T>(1);
                } else {
                    valueList = (ArrayList<T>) dataEntryResult.getValue().getValue();
                    version = dataEntryResult.getValue().getVersion();
                }
                valueList.add(message);

                ResultCode put = put(key, valueList, version, expireTime);
                if (!put.isSuccess()) {
                    version++;
                } else {
                    logger.debug("add server aggregate to tair success: {}", message.toString());
                    break;
                }
            } else {
                logger.warn("store info to tair error . key: {}", key);
                break;
                // throw new Exception("connected to  tair server error !");
            }
        }

    }

    /**
     * 并发的添加到缓存中,缓存的对象是hashmap,添加失败的话，仅仅打日志,不抛出异常
     * 
     * @param tairKey 缓存的key
     * @param mapKey map的key
     * @param objectBuilder 对象构建
     */
    public void concurrentHashMapPutSilence(String tairKey, String mapKey, ObjectBuilder objectBuilder) {

        int version = 1;
        while (true) {

            Result<DataEntry> dataEntryResult = get(tairKey);
            if (dataEntryResult.isSuccess()) {
                HashMap<String, Serializable> valueMap = null;
                if (dataEntryResult.getValue() == null || dataEntryResult.getValue().getValue() == null) {
                    valueMap = new HashMap<String, Serializable>(1);
                } else {
                    valueMap = (HashMap<String, Serializable>) dataEntryResult.getValue().getValue();
                    version = dataEntryResult.getValue().getVersion();
                }

                valueMap.put(mapKey, objectBuilder.buildObject());

                ResultCode put = put(tairKey, valueMap, version);
                if (!put.isSuccess()) {
                    version++;
                } else {
                    logger.debug("add server aggregate to tair success: {}", objectBuilder.toString());
                    break;
                }
            } else {
                logger.warn("store info to tair error . key: {}", tairKey);
                break;
                // throw new Exception("connected to  tair server error !");
            }
        }

    }

    /**
     * 并发的添加到缓存中,缓存的对象是hashmap,添加失败的话，仅仅打日志,不抛出异常
     * 
     * @param tairKey 缓存的key
     * @param mapKey map的key
     * @param value 对象值
     */
    public <T extends Serializable> void concurrentHashMapPutSilence(String tairKey, String mapKey, T value) {

        int version = 1;
        while (true) {

            Result<DataEntry> dataEntryResult = get(tairKey);
            if (dataEntryResult.isSuccess()) {
                HashMap<String, T> valueMap = null;
                if (dataEntryResult.getValue() == null || dataEntryResult.getValue().getValue() == null) {
                    valueMap = new HashMap<String, T>(1);
                } else {
                    valueMap = (HashMap<String, T>) dataEntryResult.getValue().getValue();
                    version = dataEntryResult.getValue().getVersion();
                }

                valueMap.put(mapKey, value);

                ResultCode put = put(tairKey, valueMap, version);
                if (!put.isSuccess()) {
                    version++;
                } else {
                    logger.debug("add server aggregate to tair success: {}", value);
                    break;
                }
            } else {
                logger.warn("store info to tair error . key: {} ", tairKey);
                break;
            }
        }

    }

    public static boolean isSuccess(Result<DataEntry> server) {

        return server != null && server.isSuccess() && server.getRc().getCode() == ResultCode.SUCCESS.getCode()
               && server.getValue() != null;
    }

    public static void main(String[] args) throws Exception {

        Properties properties = PropertiesUtils.getProperties();
        TairManagerProxy proxy = TairManagerProxy.newBuilder().withProperties(properties).build();

        // ResultCode delete = proxy.delete(serverAggregateKey4Tair);
        // System.out.println(delete.isSuccess() + ":" + delete.getCode());
        // Result<DataEntry> dataEntryResult = proxy.get(serverAggregateKey4Tair);
        // HashMap<String, RemoteServerVo> serverMap = dataEntryResult.getValue().getValue();

        // String key = "13588701564";
        // Result<DataEntry> test = proxy.get("jjnew");
        // System.out.println("result :" + test.isSuccess() + " code :" + test.getRc().getCode() + " value:"
        // + test.getValue() + "version :" + test.getRc());
        // System.out.println("-----------------");
        //
        // ResultCode put = proxy.put("jjnew", "gg3", 1);
        // System.out.println(put.isSuccess() + " : " + put.getCode() + " :  " + put.getMessage());
        // System.out.println("-----------------");
        //
        // Result<DataEntry> jjnew = proxy.get("jjnew");
        // System.out.println("result :" + jjnew.isSuccess() + " code :" + jjnew.getRc().getCode() + " value:"
        // + jjnew.getValue() + "version :" + jjnew.getRc());

        // Result<DataEntry> dataEntryResult = proxy.get(key);
        // System.out.println(dataEntryResult.isSuccess() + " : " + dataEntryResult.getRc().getCode() + " :  "
        // + dataEntryResult.getRc().getMessage());
        // UserLocationVo userLocationVo = (UserLocationVo) dataEntryResult.getValue().getValue();
        // System.out.println(userLocationVo.toString());

        // ResultCode test = proxy.put("test", null);
        // System.out.println(test.isSuccess() + " : " + test.getCode() + " :  " + test.getMessage());
        //
        // Result<DataEntry> test1 = proxy.get("test");
        // if (test1 == null) {
        // System.out.println("it is empty!");
        //
        // } else {
        // DataEntry value = test1.getValue();
        // Object value1 = value.getValue();
        // System.out.println(value1.toString());
        // long version = value.getVersion();
        // System.out.println(version);
        // Object key = value.getKey();
        // System.out.println(key.toString());
        // }

        // proxy.close();

    }
}
