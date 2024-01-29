package com.wcy.woj.model.dto.solution;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑请求
 *

 */
@Data
public class SolutionEditRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 特殊标签列表
     */
    private List<String> specialTags;

    private static final long serialVersionUID = 1L;
}