package com.pajk.diablo.im.common.enums;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 下午2:43
 * </pre>
 */
public enum RequestTypeEnum {

    REGISTER(1, "注册"),

    TALK(2, "对话"),

    MESSAGE_TRANSFER(3, "消息的转移"),

    GROUP_TALK(4, "群组对话");

    private int    type;

    private String desc;

    RequestTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {

        return type;
    }

    public static RequestTypeEnum getRequestTypeEnum(int type) {

        RequestTypeEnum[] values = RequestTypeEnum.values();
        for (RequestTypeEnum value : values) {
            if (value.getType() == type) {
                return value;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "RequestTypeEnum{" + "type=" + type + ", desc='" + desc + '\'' + '}';
    }
}
