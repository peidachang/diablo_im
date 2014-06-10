package com.pajk.diablo.im.common.store;

import java.util.ArrayList;

/**
 * <pre>
 * Created by zhaoming on 14-6-5 下午5:59
 * </pre>
 */
public class ArrayListMapper implements ObjectMapper<ArrayList> {

    // todo 待实现一个更高效的实现
    private ArrayList<Object> arrayList;

    @Override
    public ArrayList initObject() {
        arrayList = new ArrayList<Object>(1);
        return arrayList;
    }

    @Override
    public void addObject(Object value) {
        arrayList.add(value);
    }

    @Override
    public ArrayList returnObject() {
        return arrayList;
    }
}
