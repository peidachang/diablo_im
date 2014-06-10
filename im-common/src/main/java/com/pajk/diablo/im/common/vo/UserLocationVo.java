package com.pajk.diablo.im.common.vo;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 上午10:30
 * </pre>
 */
public class UserLocationVo implements Serializable {

    private static final long serialVersionUID = 4073857250891767953L;
    private String            userId;

    private String            machineHost;

    private int               machinePort;

    private String            uri;

    public UserLocationVo() {

    }

    public UserLocationVo(String userId, String machineHost, int machinePort) {

        this.userId = userId;
        this.machineHost = machineHost;
        this.machinePort = machinePort;
    }

    public String getMachineHost() {
        return machineHost;
    }

    public void setMachineHost(String machineHost) {
        this.machineHost = machineHost;
    }

    public int getMachinePort() {
        return machinePort;
    }

    public void setMachinePort(int machinePort) {
        this.machinePort = machinePort;
    }

    public String getUri() {

        if (StringUtils.isEmpty(uri)) {
            uri = machineHost + ":" + machinePort;
        }
        return uri;
    }

    public boolean isNull() {
        return false;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "UserLocationVo{" + "userId='" + userId + '\'' + ", machineHost='" + machineHost + '\''
               + ", machinePort=" + machinePort + ", uri='" + uri + '\'' + '}';
    }
}
