package com.pajk.diablo.im.common.util;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

/**
 * <pre>
 * Created by zhaoming on 14-5-29 下午7:25
 * 日志初始化的工具类
 * </pre>
 */
public abstract class LogUtils {

    /**
     * 初始化日志信息
     */
    public static void initLogback() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            lc.reset();
            configurator.setContext(lc);
            configurator.doConfigure(ResourceUtils.getURL("classpath:Logback.xml"));
        } catch (Exception je) {
            je.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {

        LogUtils.initLogback();
        // URL resource = LogUtils.class.getResource("Logback.xml");
        //
        // String s = resource.getContent().toString();
        // System.out.println(s);
    }
}
