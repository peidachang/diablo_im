package com.pajk.diablo.im.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * <pre>
 * Created by zhaoming on 14-5-30 上午11:27
 * </pre>
 */
public abstract class PropertiesUtils {

    private static final String linuxPropertiesDir   = "/home/admin/im-config/im-server.properties";
    private static final String windowsPropertiesDir = "C:\\im-config/im-server.properties";

    public static Properties getProperties() throws IOException {
        String osName = System.getProperties().getProperty("os.name");
        String fileDIr = osName.toLowerCase().contains("window") ? windowsPropertiesDir : linuxPropertiesDir;

        Properties properties = new Properties();
        properties.load(new FileInputStream(fileDIr));
        return properties;
    }
}
