package com.wcy.woj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目提交编程语言枚举
 */
public enum QuestionSubmitLanguageEnum {

    JAVA("java"),
    CPLUSPLUS("cpp"),
    C("c"),
    PYTHON("python"),
    JSON("json"),
    GOLANG("go");

    private final String value;


    QuestionSubmitLanguageEnum(String text) {
        this.value = text;
    }


    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static QuestionSubmitLanguageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionSubmitLanguageEnum anEnum : QuestionSubmitLanguageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }


    public String getValue() {
        return value;
    }
}
