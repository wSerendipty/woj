package com.wcy.woj.model.dto.questionsubmit;

import com.wcy.woj.model.dto.question.JudgeCase;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
*
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题目类型 normal / detail / contest
     */
    private String type;


    private static final long serialVersionUID = 1L;
}