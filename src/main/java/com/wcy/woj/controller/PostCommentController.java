package com.wcy.woj.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wcy.woj.common.BaseResponse;
import com.wcy.woj.common.DeleteRequest;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.common.ResultUtils;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.exception.ThrowUtils;
import com.wcy.woj.model.dto.postComment.PostCommentAddRequest;
import com.wcy.woj.model.dto.postComment.PostCommentQueryRequest;
import com.wcy.woj.model.entity.PostComment;
import com.wcy.woj.model.entity.User;
import com.wcy.woj.model.vo.PostCommentVO;
import com.wcy.woj.service.PostCommentService;
import com.wcy.woj.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/6 21:37
 */
@RestController
@RequestMapping("/postComment")
public class PostCommentController {
    @Resource
    private PostCommentService postCommentService;
    @Resource
    private UserService userService;

    /**
     * 分页获取帖子评论列表
     *
     * @param postCommentQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PostCommentVO>> getPostCommentList(@RequestBody PostCommentQueryRequest postCommentQueryRequest, HttpServletRequest request) {
        long current = postCommentQueryRequest.getCurrent();
        long size = postCommentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<PostComment> postCommentPage = postCommentService.page(new Page<>(current, size),
                postCommentService.getQueryWrapper(postCommentQueryRequest));
        return ResultUtils.success(postCommentService.getPostCommentVOList(postCommentPage, request));
    }

    /**
     * 分页 通过父评论 id 获取子评论列表
     *
     * @param postCommentQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo/parent")
    public BaseResponse<Page<PostCommentVO>> getPostCommentListByParentId(@RequestBody PostCommentQueryRequest postCommentQueryRequest, HttpServletRequest request) {
        long current = postCommentQueryRequest.getCurrent();
        long size = postCommentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<PostComment> postCommentPage = postCommentService.page(new Page<>(current, size),
                postCommentService.getQueryWrapper(postCommentQueryRequest));
        return ResultUtils.success(postCommentService.getPostCommentVOListByParentId(postCommentQueryRequest.getCommentId(), postCommentPage, request));
    }

    /**
     * 评论帖子
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPostComment(@RequestBody PostCommentAddRequest postCommentAddRequest, HttpServletRequest request) {
        if (postCommentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PostComment postComment = new PostComment();
        BeanUtil.copyProperties(postCommentAddRequest, postComment);
        postCommentService.validPost(postComment, true);
        User loginUser = userService.getLoginUser(request);
        postComment.setUserId(loginUser.getId());
        Long newPostId = postCommentService.doPostComment(postComment);
        return ResultUtils.success(newPostId);
    }

    /**
     * 删除帖子评论
     */
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deletePostComment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long postCommentId = deleteRequest.getId();
        Boolean result = postCommentService.doDeletePostComment(postCommentId, request);
        return ResultUtils.success(result);
    }
}
