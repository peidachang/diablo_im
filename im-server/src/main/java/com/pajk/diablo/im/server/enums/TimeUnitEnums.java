package com.pajk.diablo.im.server.enums;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 下午5:40
 * </pre>
 */
public enum TimeUnitEnums {

    SECONDS(TimeUnit.SECONDS, "seconds"),

    MINUTES(TimeUnit.MINUTES, "minutes"),

    HOURS(TimeUnit.HOURS, "hours");

    private TimeUnit timeUnit;

    private String   type;

    TimeUnitEnums(TimeUnit timeUnit, String type) {
        this.timeUnit = timeUnit;
        this.type = type;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public String getType() {
        return type;
    }

    public static TimeUnit loadTimeUnit(String type) {

        if (StringUtils.isBlank(type)) {
            return TimeUnit.SECONDS;
        }
        TimeUnitEnums[] values = TimeUnitEnums.values();
        for (TimeUnitEnums value : values) {
            if (value.getType().equals(type)) {
                return value.getTimeUnit();
            }
        }

        return TimeUnitEnums.SECONDS.getTimeUnit();

    }
}
