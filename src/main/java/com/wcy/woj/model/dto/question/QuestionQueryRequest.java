package com.wcy.woj.model.dto.question;

import com.wcy.woj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
*
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 题目难度（简单、中等、困难）
     */
    private String difficulty;

    /**
     * 题解数
     */
    private Integer solutionNum;


    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;


    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}