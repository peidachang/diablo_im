package com.pajk.diablo.im.common.store;

import java.io.Serializable;

/**
 * <pre>
 * Created by zhaoming on 14-6-5 下午7:06
 * todo 待改进
 * </pre>
 */
public interface ObjectBuilder<T extends Serializable> {

    /**
     * 返回数据
     * 
     * @return
     */
    T buildObject();
}
