package com.wcy.woj.service;

import com.wcy.woj.model.entity.PostCommentThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcy.woj.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 王长远
* @description 针对表【post_comment_thumb(帖子评论点赞)】的数据库操作Service
* @createDate 2024-01-06 20:05:28
*/
public interface PostCommentThumbService extends IService<PostCommentThumb> {

    int doPostCommentThumb(long postId, User loginUser);

    @Transactional(rollbackFor = Exception.class)
    int doPostCommentThumbInner(long userId, long postId);
}
