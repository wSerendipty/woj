package com.wcy.woj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.constant.CommonConstant;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.exception.ThrowUtils;
import com.wcy.woj.mapper.PostCommentThumbMapper;
import com.wcy.woj.mapper.PostMapper;
import com.wcy.woj.model.dto.post.PostQueryRequest;
import com.wcy.woj.model.dto.postComment.PostCommentQueryRequest;
import com.wcy.woj.model.entity.*;
import com.wcy.woj.model.vo.PostCommentVO;
import com.wcy.woj.model.vo.PostVO;
import com.wcy.woj.model.vo.UserVO;
import com.wcy.woj.service.PostCommentService;
import com.wcy.woj.mapper.PostCommentMapper;
import com.wcy.woj.service.PostService;
import com.wcy.woj.service.UserService;
import com.wcy.woj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 王长远
 * @description 针对表【post_comment(帖子评论)】的数据库操作Service实现
 * @createDate 2024-01-06 20:05:20
 */
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
        implements PostCommentService {
    @Resource
    private PostCommentMapper postCommentMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostCommentThumbMapper postCommentThumbMapper;

    @Resource
    private UserService userService;

    private static int deleteNum = 0;

    @Override
    public void validPost(PostComment postComment, boolean add) {
        if (postComment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String content = postComment.getContent();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param postCommentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<PostComment> getQueryWrapper(PostCommentQueryRequest postCommentQueryRequest) {
        QueryWrapper<PostComment> queryWrapper = new QueryWrapper<>();
        if (postCommentQueryRequest == null) {
            return queryWrapper;
        }

        Long postId = postCommentQueryRequest.getPostId();
        Integer status = postCommentQueryRequest.getStatus();
        Long commentId = postCommentQueryRequest.getCommentId();
        String sortField = postCommentQueryRequest.getSortField();
        String sortOrder = postCommentQueryRequest.getSortOrder();
        queryWrapper.eq(ObjectUtils.isNotEmpty(postId), "postId", postId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(commentId), "parentId", commentId);
        queryWrapper.isNull(ObjectUtils.isEmpty(commentId), "parentId");
        queryWrapper.orderBy(ObjectUtils.isEmpty(sortField), false, "createTime");
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取帖子评论封装 分页
     * @param postCommentPage
     * @param request
     * @return
     */
    @Override
    public Page<PostCommentVO> getPostCommentVOList(Page<PostComment> postCommentPage, HttpServletRequest request) {
        List<PostComment> commentList = postCommentPage.getRecords();
        Page<PostCommentVO> postVOPage = new Page<>(postCommentPage.getCurrent(), postCommentPage.getSize(), postCommentPage.getTotal());
        if (CollectionUtils.isEmpty(commentList)) {
            return postVOPage;
        }

        //  1. 关联查询用户信息
        Set<Long> userIds = commentList.stream().map(PostComment::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIds).stream().collect(Collectors.groupingBy(User::getId));

        // 2. 已登录获取用户是否点赞 是否回复
        Map<Long, Boolean> commentIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> commentIdHasReplyMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            commentList.stream().map(PostComment::getId).forEach(commentId -> {
                // 获取点赞
                QueryWrapper<PostCommentThumb> postCommentThumbQueryWrapper = new QueryWrapper<>();
                postCommentThumbQueryWrapper.eq("commentId", commentId);
                postCommentThumbQueryWrapper.eq("userId", loginUser.getId());
                List<PostCommentThumb> postCommentThumbList = postCommentThumbMapper.selectList(postCommentThumbQueryWrapper);
                commentIdHasThumbMap.put(commentId, CollectionUtils.isNotEmpty(postCommentThumbList));
                // 获取回复
                QueryWrapper<PostComment> postCommentQueryWrapper = new QueryWrapper<>();
                postCommentQueryWrapper.eq("parentId", commentId);
                postCommentQueryWrapper.eq("userId", loginUser.getId());
                List<PostComment> postCommentList = postCommentMapper.selectList(postCommentQueryWrapper);
                commentIdHasReplyMap.put(commentId, CollectionUtils.isNotEmpty(postCommentList));
            });
        }

        // 3. 封装数据
        List<PostCommentVO> postCommentVOList = commentList.stream().map(postComment -> {
            PostCommentVO postCommentVO = new PostCommentVO();
            BeanUtil.copyProperties(postComment, postCommentVO);
            Long userId = postComment.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            postCommentVO.setCreateUser(userService.getUserVO(user));
            postCommentVO.setHasThumb(commentIdHasThumbMap.get(postComment.getId()));
            postCommentVO.setHasReply(commentIdHasReplyMap.get(postComment.getId()));
            return postCommentVO;
        }).collect(Collectors.toList());

        postVOPage.setRecords(postCommentVOList);
        return postVOPage;
    }

    /**
     * 通过评论 id 获取所有子评论 分页
     * @param parentId
     * @param postCommentPage
     * @param request
     * @return
     */
    @Override
    public Page<PostCommentVO> getPostCommentVOListByParentId(long parentId, Page<PostComment> postCommentPage, HttpServletRequest request) {
        List<PostComment> commentList = postCommentPage.getRecords();
        Page<PostCommentVO> postVOPage = new Page<>(postCommentPage.getCurrent(), postCommentPage.getSize(), postCommentPage.getTotal());
        if (CollectionUtils.isEmpty(commentList)) {
            return postVOPage;
        }

        //  1. 关联查询用户信息
        Set<Long> userIds = commentList.stream().map(PostComment::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIds).stream().collect(Collectors.groupingBy(User::getId));

        Set<Long> toUserIds = commentList.stream().map(PostComment::getToUserId).collect(Collectors.toSet());
        Map<Long, List<User>> toUserIdUserListMap = userService.listByIds(toUserIds).stream().collect(Collectors.groupingBy(User::getId));

        // 2. 已登录获取用户是否点赞 是否回复
        Map<Long, Boolean> commentIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> commentIdHasReplyMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            commentList.stream().map(PostComment::getId).forEach(commentId -> {
                // 获取点赞
                QueryWrapper<PostCommentThumb> postCommentThumbQueryWrapper = new QueryWrapper<>();
                postCommentThumbQueryWrapper.eq("commentId", commentId);
                postCommentThumbQueryWrapper.eq("userId", loginUser.getId());
                List<PostCommentThumb> postCommentThumbList = postCommentThumbMapper.selectList(postCommentThumbQueryWrapper);
                commentIdHasThumbMap.put(commentId, CollectionUtils.isNotEmpty(postCommentThumbList));
                // 获取回复
                QueryWrapper<PostComment> postCommentQueryWrapper = new QueryWrapper<>();
                postCommentQueryWrapper.eq("parentId", commentId);
                postCommentQueryWrapper.eq("userId", loginUser.getId());
                List<PostComment> postCommentList = postCommentMapper.selectList(postCommentQueryWrapper);
                commentIdHasReplyMap.put(commentId, CollectionUtils.isNotEmpty(postCommentList));
            });
        }

        // 3. 封装数据
        List<PostCommentVO> postCommentVOList = commentList.stream().map(postComment -> {
            PostCommentVO postCommentVO = new PostCommentVO();
            BeanUtil.copyProperties(postComment, postCommentVO);
            Long userId = postComment.getUserId();
            User user = null;
            Long toUserId = postComment.getToUserId();
            User toUser = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            if (toUserIdUserListMap.containsKey(toUserId)) {
                toUser = toUserIdUserListMap.get(toUserId).get(0);
            }
            postCommentVO.setCreateUser(userService.getUserVO(user));
            postCommentVO.setToUser(userService.getUserVO(toUser));
            postCommentVO.setHasThumb(commentIdHasThumbMap.get(postComment.getId()));
            postCommentVO.setHasReply(commentIdHasReplyMap.get(postComment.getId()));
            return postCommentVO;
        }).collect(Collectors.toList());

        postVOPage.setRecords(postCommentVOList);
        return postVOPage;
    }

    /**
     * 评论帖子
     *
     * @param postComment
     * @return
     */
    @Override
    public Long doPostComment(PostComment postComment) {
        Long postId = postComment.getPostId();
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }
        Long parentId = postComment.getParentId();
        if (parentId != null && parentId > 0) {
            PostComment parentPostComment = postCommentMapper.selectById(parentId);
            if (parentPostComment == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "父评论不存在");
            }
        }
        // TODO: 帖子评论审核状态
        postComment.setStatus(1);
        postComment.setThumbNum(0);
        postComment.setReplyNum(0);
        boolean result = this.save(postComment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 更新父评论回复数
        if (parentId != null && parentId > 0) {
            PostComment parentPostComment = postCommentMapper.selectById(parentId);
            parentPostComment.setReplyNum(parentPostComment.getReplyNum() + 1);
            result = this.updateById(parentPostComment);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        }
        // 更新帖子评论数
        post.setCommentNum(post.getCommentNum() + 1);
        result = postMapper.updateById(post) > 0;
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return postComment.getId();
    }

    /**
     * 删除帖子评论
     *
     * @param postCommentId
     * @param request
     * @return
     */
    @Override
    public Boolean doDeletePostComment(long postCommentId, HttpServletRequest request) {
        PostComment postComment = postCommentMapper.selectById(postCommentId);
        if (postComment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        }
        User loginUser = userService.getLoginUser(request);
        // 仅本人或管理员可删除
        if (!postComment.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        int r = deleteComment(postCommentId);
        deleteNum = 0;
        return r > 0;
    }

    /**
     * 删除帖子评论
     */
    public int deleteComment(Long commentId) {
        // 判断是否有子评论 ,如果有则递归删除子评论，否则直接删除
        List<PostComment> childComments = postCommentMapper.selectList(new QueryWrapper<PostComment>().eq("parentId", commentId));
        if (childComments.size() > 0) {
            for (PostComment childComment : childComments) {
                deleteComment(childComment.getId());
            }
        }
        PostComment postComment = postCommentMapper.selectById(commentId);
        boolean b = this.removeById(commentId);
        if (b) {
            deleteNum++;
            // 更新帖子评论数
            if (postComment != null) {
                Post post = postMapper.selectById(postComment.getPostId());
                post.setCommentNum(post.getCommentNum() - 1);
                postMapper.updateById(post);
            }
            // 更新父评论回复数
            if (postComment.getParentId() != null && postComment.getParentId() > 0) {
                PostComment parentPostComment = postCommentMapper.selectById(postComment.getParentId());
                parentPostComment.setReplyNum(parentPostComment.getReplyNum() - 1);
                this.updateById(parentPostComment);
            }
        }
        return deleteNum;
    }


}




