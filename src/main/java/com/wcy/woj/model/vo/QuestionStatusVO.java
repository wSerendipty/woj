package com.wcy.woj.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * 题目完成状态封装类
 * @TableName question_status
 */
@Data
public class QuestionStatusVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 完成的题目 类型 normal / daily /contest
     */
    private String type;

    /**
     * 题目id
     */
    private Long questionId;

    private Integer status;


    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    private static final long serialVersionUID = 1L;
}