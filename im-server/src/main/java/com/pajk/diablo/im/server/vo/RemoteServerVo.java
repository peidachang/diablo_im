package com.pajk.diablo.im.server.vo;

import java.io.Serializable;

/**
 * <pre>
 * Created by zhaoming on 14-5-28 上午9:12
 * </pre>
 */
public class RemoteServerVo implements Serializable {

    private static final long serialVersionUID = 7409179750921535100L;

    private String            machineHost;

    private int               machinePort;

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

    @Override
    public String toString() {
        return "RemoteServerVo{" + "machineHost='" + machineHost + '\'' + ", machinePort=" + machinePort + '}';
    }
}
