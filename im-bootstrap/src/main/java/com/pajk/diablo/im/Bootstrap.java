package com.pajk.diablo.im;

import com.pajk.diablo.im.common.util.LogUtils;
import com.pajk.diablo.im.server.core.ServerFacade;

/**
 * <pre>
 * Created by zhaoming on 14-5-30 下午2:34
 * </pre>
 */
public class Bootstrap {

    public static void main(String[] args) {

        LogUtils.initLogback();
        ServerFacade wsServerFacade = new ServerFacade();
        try {
            wsServerFacade.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
}
