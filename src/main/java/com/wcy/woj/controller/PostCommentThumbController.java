package com.wcy.woj.controller;

import com.wcy.woj.common.BaseResponse;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.common.ResultUtils;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.model.dto.postCommentThumb.PostCommentThumbAddRequest;
import com.wcy.woj.model.dto.postthumb.PostThumbAddRequest;
import com.wcy.woj.model.entity.User;
import com.wcy.woj.service.PostCommentService;
import com.wcy.woj.service.PostCommentThumbService;
import com.wcy.woj.service.PostThumbService;
import com.wcy.woj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞接口
 *

 */
@RestController
@RequestMapping("/post/comment_thumb")
@Slf4j
public class PostCommentThumbController {

    @Resource
    private PostCommentThumbService postCommentThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postCommentThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostCommentThumbAddRequest postCommentThumbAddRequest,
            HttpServletRequest request) {
        if (postCommentThumbAddRequest == null || postCommentThumbAddRequest.getCommentId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long commentId = postCommentThumbAddRequest.getCommentId();
        int result = postCommentThumbService.doPostCommentThumb(commentId, loginUser);
        return ResultUtils.success(result);
    }

}
