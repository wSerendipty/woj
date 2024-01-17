package com.wcy.woj.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子评论
 */
@Data
public class PostCommentVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 帖子 id
     */
    private Long postId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 父评论 id
     */
    private Long parentId;

    /**
     * 给哪个id的评论
     */
    private Long toUserId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 回复数
     */
    private Integer replyNum;

    /**
     * 是否已点赞
     */
    private Boolean hasThumb;

    /**
     * 是否已回复
     */
    private Boolean hasReply;

    /**
     * 评论状态（0 - 待审核、1 - 审核通过、2 - 审核不通过）
     */
    private Integer status;

    /**
     * 创建人信息
     */
    private UserVO createUser;

    /**
     * 给哪个id的评论用户信息
     */
    private UserVO toUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}