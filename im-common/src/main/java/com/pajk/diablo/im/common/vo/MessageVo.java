package com.pajk.diablo.im.common.vo;

import java.util.Map;

/**
 * <pre>
 * Created by zhaoming on 14-6-3 下午3:04
 * </pre>
 */
public class MessageVo {

    // 消息类型
    private int                 type;

    private String              fromUserId;

    private String              toUserId;

    private String              msg;

    // 变量数据
    private Map<String, Object> datas;

    public int getType() {
        return type;
    }

    /**
     * @param type 消息类型 @RequestTypeEnum
     */
    public void setType(int type) {
        this.type = type;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getDatas() {
        return datas;
    }

    public void setDatas(Map<String, Object> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "MessageVo{" + "msg='" + msg + '\'' + ", toUserId='" + toUserId + '\'' + ", fromUserId='" + fromUserId
               + '\'' + ", type=" + type + '}';
    }
}
