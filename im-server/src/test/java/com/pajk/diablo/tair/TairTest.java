package com.pajk.diablo.tair;

import java.util.ArrayList;
import java.util.Properties;

import com.pajk.diablo.im.common.util.KeyGenerate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pajk.diablo.im.common.store.TairManagerProxy;
import com.pajk.diablo.im.common.util.PropertiesUtils;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

/**
 * <pre>
 * Created by zhaoming on 14-6-4 下午5:22
 * </pre>
 */
public class TairTest {

    private static final Logger logger = LoggerFactory.getLogger(TairTest.class.getName());

    public static void main(String[] args) throws Exception {

        Properties properties = PropertiesUtils.getProperties();
        TairManagerProxy proxy = TairManagerProxy.newBuilder().withProperties(properties).build();

        // ArrayList<String> strings = new ArrayList<String>(2);
        //
        // strings.add("13588701163");
        // strings.add("13588701164");
        // strings.add("13588701165");
        // strings.add("13588701166");
        //
        // ResultCode put = proxy.put("123", strings);
        // System.out.println(put.isSuccess() + ":" + put.getCode() + ":" + put.getMessage());
        //
        // Result<DataEntry> dataEntryResult = proxy.get("123");
        // if (TairManagerProxy.isSuccess(dataEntryResult)) {
        // ArrayList<String> results = (ArrayList<String>) dataEntryResult.getValue().getValue();
        // for (String result : results) {
        // System.out.println(result);
        // }
        // } else {
        // System.out.println("empty!");
        // }

        // delInfo(proxy);

//        lookUnReadmsg(proxy);

        deleteServerA(proxy);
        // if (TairManagerProxy.isSuccess(dataEntryResult)) {
        // dataEntryResult.
        // }

        // LogUtils.initLogback();
        //
        // logger.info("hehe {} -- {}", "zhaoming", "xue");

    }

    private static void lookUnReadmsg(TairManagerProxy proxy) {
        String key = KeyGenerate.userUnreadMsgKeyGenerate("13588701165");

        // String serverAggregateKey4Tair = KeyGenerate.getServerAggregateKey4Tair();
        Result<DataEntry> dataEntryResult = proxy.get(key);
        System.out.println(dataEntryResult.isSuccess() + ":" + dataEntryResult.getRc().isSuccess() + ":"
                           + dataEntryResult.getValue());
        ArrayList<String> value = (ArrayList<String>) dataEntryResult.getValue().getValue();
        for (String s : value) {
            System.out.println(s);
        }
    }

    public static void deleteServerA(TairManagerProxy proxy) {
        String serverAggregateKey4Tair = KeyGenerate.getServerAggregateMonitorKey();
        ResultCode delete = proxy.delete(serverAggregateKey4Tair);
        System.out.println(delete.getCode() + ":" + delete.getMessage());

        Result<DataEntry> dataEntryResult = proxy.get(serverAggregateKey4Tair);
        if (TairManagerProxy.isSuccess(dataEntryResult)) {
            System.out.println(dataEntryResult.getValue());
        } else {
            System.out.println("empty!");
        }
    }

    private static void delInfo(TairManagerProxy proxy) {
        String[] ids = new String[] { "13588701651", "1358870166588701663", "13588701654", "13588701655" };
        for (String id : ids) {
            Result<DataEntry> dataEntryResult = proxy.get(id);
            if (TairManagerProxy.isSuccess(dataEntryResult)) {
                Object value = dataEntryResult.getValue().getValue();
                System.out.println(value);
            } else {
                System.out.println("empty!");
            }

            proxy.delete(id);

            Result<DataEntry> dataEntryResult1 = proxy.get(id);
            if (TairManagerProxy.isSuccess(dataEntryResult1)) {
                Object value = dataEntryResult1.getValue().getValue();
                System.out.println(value);
            } else {
                System.out.println("empty!");
            }
        }
    }
}
