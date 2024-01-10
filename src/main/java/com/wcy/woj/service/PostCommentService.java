package com.wcy.woj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcy.woj.model.dto.post.PostQueryRequest;
import com.wcy.woj.model.dto.postComment.PostCommentQueryRequest;
import com.wcy.woj.model.entity.Post;
import com.wcy.woj.model.entity.PostComment;
import com.wcy.woj.model.vo.PostCommentVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 王长远
* @description 针对表【post_comment(帖子评论)】的数据库操作Service
* @createDate 2024-01-06 20:05:20
*/
public interface PostCommentService extends IService<PostComment> {
    void validPost(PostComment postComment, boolean add);

    QueryWrapper<PostComment> getQueryWrapper(PostCommentQueryRequest postCommentQueryRequest);

    /**
     * 获取帖子评论封装
     * @param postCommentPage
     * @return
     */
    Page<PostCommentVO> getPostCommentVOList(Page<PostComment> postCommentPage, HttpServletRequest request);

    /**
     * 通过评论 id 获取所有子评论 分页
     */
    Page<PostCommentVO> getPostCommentVOListByParentId(long parentId, Page<PostComment> postCommentPage, HttpServletRequest request);


    /**
     * 评论帖子
     */
    Long doPostComment(PostComment postComment);

    /**
     * 删除帖子评论
     */
    Boolean doDeletePostComment(long postCommentId, HttpServletRequest request);

}
