package com.pajk.diablo.im.common.store;

import com.pajk.diablo.im.common.util.KeyGenerate;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * Created by zhaoming on 14-6-5 下午7:35
 * </pre>
 */
public class UserMessageManager {

    /**
     * 将未读消息放到tair中
     * 
     * @param tairManagerProxy tair的代理
     * @param key tair中的key
     * @param message 欲放到tair中的值
     * @param <T>
     */
    public static <T extends Serializable> void storeUnreadMsg2Tair(TairManagerProxy tairManagerProxy, String key,
                                                                    T message) {
        tairManagerProxy.concurrentArrayListPutSilence(KeyGenerate.userUnreadMsgKeyGenerate(key), message,
                                                       TairManagerProxy.ONE_DAY);
    }

    /**
     * 从tair中获取未读消息
     * 
     * @param tairManagerProxy tair的代理
     * @param key tair中的key
     * @return
     */
    public static List<String> getUnreadMsg2Tair(TairManagerProxy tairManagerProxy, String key) {

        Result<DataEntry> dataEntryResult = tairManagerProxy.get(KeyGenerate.userUnreadMsgKeyGenerate(key));
        if (!TairManagerProxy.isSuccess(dataEntryResult)) {
            return Collections.EMPTY_LIST;
        }

        return (List<String>) dataEntryResult.getValue().getValue();
    }
}
