package com.wcy.woj.model.dto.questionStatus;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目题解
 */
@Data
public class QuestionStatusQueryRequest  implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 完成的题目 类型 normal / daily /contest
     */
    private String type;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;
    


    private static final long serialVersionUID = 1L;
}