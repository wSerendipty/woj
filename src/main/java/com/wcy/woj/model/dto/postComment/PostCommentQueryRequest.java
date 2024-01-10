package com.wcy.woj.model.dto.postComment;

import com.wcy.woj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *

 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentQueryRequest extends PageRequest implements Serializable {


    /**
     * 帖子 id
     */
    private Long postId;

    /**
     * 评论 id
     */
    private Long commentId;

    /**
     * 帖子状态
     */
    private Integer status;



    private static final long serialVersionUID = 1L;
}