package com.pajk.diablo.im.common.store;

import java.io.Serializable;

/**
 * <pre>
 * Created by zhaoming on 14-6-5 下午5:49
 * todo 待改进
 * </pre>
 */
public interface ObjectMapper<T extends Serializable> {

    /**
     * 对象的构建
     * 
     * @return 指定类型的对象
     */
    T initObject();

    /**
     * 添加数据
     * 
     * @param value 需要添加的数据
     */
    void addObject(Object value);

    /**
     * 返回数据
     * 
     * @return
     */
    T returnObject();
}
