package com.wcy.woj.model.dto.daily;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/24 13:50
 */
@Data
public class DailyQueryRequest  implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 问题Id
     */
    private Long questionId;

    /**
     * 帖子Id
     */
    private Long postId;

    /**
     * 标签所属类型（question/post）
     */
    private String belongType;

    private Date createTime;

    private static final long serialVersionUID = 1L;

}