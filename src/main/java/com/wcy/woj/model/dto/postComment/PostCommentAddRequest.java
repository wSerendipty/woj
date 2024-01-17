package com.wcy.woj.model.dto.postComment;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *

 */
@Data
public class PostCommentAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long postId;

    /**
     * 父评论 id
     */
    private Long parentId;

    /**
     * 给哪个id的评论
     */
    private Long toUserId;

    /**
     * 内容
     */
    private String content;

    private static final long serialVersionUID = 1L;
}