package com.pajk.diablo.im.server.monitor;

import java.io.Serializable;

/**
 * <pre>
 * Created by zhaoming on 14-6-4 下午6:11
 * 数据监控对象
 * </pre>
 */
public class MonitorVo implements Serializable {

    private static final long serialVersionUID = -5932962478884955629L;
    private String            machineURI;                              // 主机地址
    private long              clientCallTotalNums;                     // 客户端数据处理的总数
    private long              clientCallFailNums;                      // 客户端数据处理的失败数
    private long              clientCallSuccessNums;                   // 客户端数据处理的成功数
    private long              serverTransferCallTotalNums;             // 服务端数据转发总数
    private long              serverTransferCallFailNums;              // 服务端数据转发失败数
    private long              serverTransferCallSuccessNums;           // 服务端数据转发成功数
    private long              serverTransferCallHandleFailNums;        // 服务端数据转发处理失败数
    private long              serverTransferCallHandleSuccessNums;     // 服务端数据转发处理成功数
    private long              clientNums;                              // 客户端连接数目

    public MonitorVo() {
    }

    public MonitorVo(String machineURI, long clientCallTotalNums, long clientCallFailNums, long clientCallSuccessNums,
                     long serverTransferCallTotalNums, long serverTransferCallFailNums,
                     long serverTransferCallSuccessNums, long serverTransferCallHandleFailNums,
                     long serverTransferCallHandleSuccessNums, long clientNums) {
        this.machineURI = machineURI;
        this.clientCallTotalNums = clientCallTotalNums;
        this.clientCallFailNums = clientCallFailNums;
        this.clientCallSuccessNums = clientCallSuccessNums;
        this.serverTransferCallTotalNums = serverTransferCallTotalNums;
        this.serverTransferCallFailNums = serverTransferCallFailNums;
        this.serverTransferCallSuccessNums = serverTransferCallSuccessNums;
        this.serverTransferCallHandleFailNums = serverTransferCallHandleFailNums;
        this.serverTransferCallHandleSuccessNums = serverTransferCallHandleSuccessNums;
        this.clientNums = clientNums;
    }

    public String getMachineURI() {
        return machineURI;
    }

    public void setMachineURI(String machineURI) {
        this.machineURI = machineURI;
    }

    public long getClientCallTotalNums() {
        return clientCallTotalNums;
    }

    public void setClientCallTotalNums(long clientCallTotalNums) {
        this.clientCallTotalNums = clientCallTotalNums;
    }

    public long getClientCallFailNums() {
        return clientCallFailNums;
    }

    public void setClientCallFailNums(long clientCallFailNums) {
        this.clientCallFailNums = clientCallFailNums;
    }

    public long getClientCallSuccessNums() {
        return clientCallSuccessNums;
    }

    public void setClientCallSuccessNums(long clientCallSuccessNums) {
        this.clientCallSuccessNums = clientCallSuccessNums;
    }

    public long getServerTransferCallTotalNums() {
        return serverTransferCallTotalNums;
    }

    public void setServerTransferCallTotalNums(long serverTransferCallTotalNums) {
        this.serverTransferCallTotalNums = serverTransferCallTotalNums;
    }

    public long getServerTransferCallFailNums() {
        return serverTransferCallFailNums;
    }

    public void setServerTransferCallFailNums(long serverTransferCallFailNums) {
        this.serverTransferCallFailNums = serverTransferCallFailNums;
    }

    public long getServerTransferCallSuccessNums() {
        return serverTransferCallSuccessNums;
    }

    public void setServerTransferCallSuccessNums(long serverTransferCallSuccessNums) {
        this.serverTransferCallSuccessNums = serverTransferCallSuccessNums;
    }

    public long getServerTransferCallHandleFailNums() {
        return serverTransferCallHandleFailNums;
    }

    public void setServerTransferCallHandleFailNums(long serverTransferCallHandleFailNums) {
        this.serverTransferCallHandleFailNums = serverTransferCallHandleFailNums;
    }

    public long getServerTransferCallHandleSuccessNums() {
        return serverTransferCallHandleSuccessNums;
    }

    public void setServerTransferCallHandleSuccessNums(long serverTransferCallHandleSuccessNums) {
        this.serverTransferCallHandleSuccessNums = serverTransferCallHandleSuccessNums;
    }

    public long getClientNums() {
        return clientNums;
    }

    public void setClientNums(long clientNums) {
        this.clientNums = clientNums;
    }

    @Override
    public String toString() {
        return "MonitorVo{" + "machineURI='" + machineURI + '\'' + ", clientCallTotalNums=" + clientCallTotalNums
               + ", clientCallFailNums=" + clientCallFailNums + ", clientCallSuccessNums=" + clientCallSuccessNums
               + ", serverTransferCallTotalNums=" + serverTransferCallTotalNums + ", serverTransferCallFailNums="
               + serverTransferCallFailNums + ", serverTransferCallSuccessNums=" + serverTransferCallSuccessNums
               + ", serverTransferCallHandleFailNums=" + serverTransferCallHandleFailNums
               + ", serverTransferCallHandleSuccessNums=" + serverTransferCallHandleSuccessNums + ", clientNums="
               + clientNums + '}';
    }
}
