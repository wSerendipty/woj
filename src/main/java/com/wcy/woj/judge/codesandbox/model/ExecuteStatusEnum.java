package com.wcy.woj.judge.codesandbox.model;

public enum ExecuteStatusEnum {
    SUCCESS("成功", 1),
    COMPILE_ERROR("编译错误", 2),
    RUNTIME_ERROR("运行时错误", 3);
    private String text;
    private Integer code;

    ExecuteStatusEnum(String text, Integer code) {
        this.text = text;
        this.code = code;
    }

    public static ExecuteStatusEnum getEnumByCode(Integer code) {
        for (ExecuteStatusEnum anEnum : ExecuteStatusEnum.values()) {
            if (anEnum.code.equals(code)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public Integer getCode() {
        return code;
    }
}
