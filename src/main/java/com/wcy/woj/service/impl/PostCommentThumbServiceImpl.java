package com.wcy.woj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.model.entity.PostComment;
import com.wcy.woj.model.entity.PostCommentThumb;
import com.wcy.woj.model.entity.User;
import com.wcy.woj.service.PostCommentService;
import com.wcy.woj.service.PostCommentThumbService;
import com.wcy.woj.mapper.PostCommentThumbMapper;
import com.wcy.woj.service.PostThumbService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author 王长远
 * @description 针对表【post_comment_thumb(帖子评论点赞)】的数据库操作Service实现
 * @createDate 2024-01-06 20:05:28
 */
@Service
public class PostCommentThumbServiceImpl extends ServiceImpl<PostCommentThumbMapper, PostCommentThumb>
        implements PostCommentThumbService {

    @Resource
    private PostCommentService postCommentService;

    /**
     * 点赞
     *
     * @param postCommentId
     * @param loginUser
     * @return
     */
    @Override
    public int doPostCommentThumb(long postCommentId, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        PostComment postComment = postCommentService.getById(postCommentId);
        if (postComment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已点赞
        long userId = loginUser.getId();
        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
        PostCommentThumbService postCommentThumbService = (PostCommentThumbService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return postCommentThumbService.doPostCommentThumbInner(userId, postCommentId);
        }
    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param commentId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostCommentThumbInner(long userId, long commentId) {
        PostCommentThumb postCommentThumb = new PostCommentThumb();
        postCommentThumb.setUserId(userId);
        postCommentThumb.setCommentId(commentId);
        QueryWrapper<PostCommentThumb> thumbQueryWrapper = new QueryWrapper<>(postCommentThumb);
        PostCommentThumb oldCommentThumb = this.getOne(thumbQueryWrapper);
        boolean result;
        // 已点赞
        if (oldCommentThumb != null) {
            result = this.remove(thumbQueryWrapper);
            if (result) {
                // 点赞数 - 1
                result = postCommentService.update()
                        .eq("id", commentId)
                        .gt("thumbNum", 0)
                        .setSql("thumbNum = thumbNum - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未点赞
            result = this.save(postCommentThumb);
            if (result) {
                // 点赞数 + 1
                result = postCommentService.update()
                        .eq("id", commentId)
                        .setSql("thumbNum = thumbNum + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }
}




