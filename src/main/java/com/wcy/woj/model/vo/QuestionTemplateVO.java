package com.wcy.woj.model.vo;

import lombok.Data;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/22 21:42
 */
@Data
public class QuestionTemplateVO {
    /**
     * 编程语言
     */
    private String language;

    /**
     * 模板代码
     */
    private String code;
}
