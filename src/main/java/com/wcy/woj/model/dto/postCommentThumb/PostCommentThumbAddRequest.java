package com.wcy.woj.model.dto.postCommentThumb;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子点赞请求
 */
@Data
public class PostCommentThumbAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long commentId;

    private static final long serialVersionUID = 1L;
}