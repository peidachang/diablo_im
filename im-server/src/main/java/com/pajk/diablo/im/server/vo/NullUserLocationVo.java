package com.pajk.diablo.im.server.vo;

import java.io.Serializable;

import com.pajk.diablo.im.common.vo.UserLocationVo;

/**
 * <pre>
 * Created by zhaoming on 14-5-27 上午10:30
 * </pre>
 */
public class NullUserLocationVo extends UserLocationVo implements Serializable {

    private static final long serialVersionUID = 4073857250891767953L;

    public boolean isNull() {
        return true;
    }
}
