package com.wcy.woj.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

@Getter
public enum WSReqTypeEnum {
    HEARTBEAT("心跳", 2),
    LOGIN("登录", 1),
    ;
    private final String text;

    private final Integer value;

    WSReqTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }


    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static WSReqTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (WSReqTypeEnum anEnum : WSReqTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
