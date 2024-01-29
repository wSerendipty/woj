package com.wcy.woj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.wcy.woj.annotation.AuthCheck;
import com.wcy.woj.common.BaseResponse;
import com.wcy.woj.common.DeleteRequest;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.common.ResultUtils;
import com.wcy.woj.constant.UserConstant;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.exception.ThrowUtils;
import com.wcy.woj.model.dto.post.PostAddRequest;
import com.wcy.woj.model.dto.post.PostEditRequest;
import com.wcy.woj.model.dto.post.PostQueryRequest;
import com.wcy.woj.model.dto.post.PostUpdateRequest;
import com.wcy.woj.model.dto.solution.SolutionAddRequest;
import com.wcy.woj.model.dto.solution.SolutionEditRequest;
import com.wcy.woj.model.dto.solution.SolutionQueryRequest;
import com.wcy.woj.model.entity.Post;
import com.wcy.woj.model.entity.Question;
import com.wcy.woj.model.entity.Solution;
import com.wcy.woj.model.entity.User;
import com.wcy.woj.model.vo.PostVO;
import com.wcy.woj.model.vo.SolutionVO;
import com.wcy.woj.service.PostService;
import com.wcy.woj.service.QuestionService;
import com.wcy.woj.service.SolutionService;
import com.wcy.woj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/question/solution")
@Slf4j
public class SolutionController {

    @Resource
    private SolutionService solutionService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();


    /**
     * 创建
     *
     * @param solutionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody SolutionAddRequest solutionAddRequest, HttpServletRequest request) {
        if (solutionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Solution solution = new Solution();
        BeanUtils.copyProperties(solutionAddRequest, solution);
        List<String> tags = solutionAddRequest.getTags();
        if (tags != null) {
            solution.setTags(GSON.toJson(tags));
        }
        List<String> specialTags = solutionAddRequest.getSpecialTags();
        if (specialTags != null) {
            solution.setSpecialTags(GSON.toJson(specialTags));
        }
        solutionService.validPost(solution, true);
        User loginUser = userService.getLoginUser(request);
        solution.setUserId(loginUser.getId());
        boolean result = solutionService.save(solution);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        Long questionId = solution.getQuestionId();
        // 更新题目题解数
        Question question = questionService.getById(questionId);
        question.setSolutionNum(question.getSolutionNum() + 1);
        boolean b = questionService.updateById(question);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        long newSolutionId = solution.getId();
        return ResultUtils.success(newSolutionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Solution oldSolution = solutionService.getById(id);
        ThrowUtils.throwIf(oldSolution == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldSolution.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = solutionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param solutionEditRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePost(@RequestBody SolutionEditRequest solutionEditRequest) {
        if (solutionEditRequest == null || solutionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Solution solution = new Solution();
        BeanUtils.copyProperties(solutionEditRequest, solution);
        List<String> tags = solutionEditRequest.getTags();
        if (tags != null) {
            solution.setTags(GSON.toJson(tags));
        }
        List<String> specialTags = solutionEditRequest.getSpecialTags();
        if (specialTags != null) {
            solution.setSpecialTags(GSON.toJson(specialTags));
        }
        if (specialTags == null) {
            solution.setSpecialTags(null);
        }
        // 参数校验
        solutionService.validPost(solution, false);
        long id = solutionEditRequest.getId();
        // 判断是否存在
        Solution oldSolution = solutionService.getById(id);
        ThrowUtils.throwIf(oldSolution == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = solutionService.updateById(solution);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<SolutionVO> getPostVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Solution solution = solutionService.getById(id);
        if (solution == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(solutionService.getSolutionVO(solution, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param solutionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SolutionVO>> listPostVOByPage(@RequestBody SolutionQueryRequest solutionQueryRequest,
                                                       HttpServletRequest request) {
        long current = solutionQueryRequest.getCurrent();
        long size = solutionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Solution> solutionPage = solutionService.page(new Page<>(current, size),
                solutionService.getQueryWrapper(solutionQueryRequest));
        return ResultUtils.success(solutionService.getSolutionVOPage(solutionPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param solutionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<SolutionVO>> listMyPostVOByPage(@RequestBody SolutionQueryRequest solutionQueryRequest,
                                                         HttpServletRequest request) {
        if (solutionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        solutionQueryRequest.setUserId(loginUser.getId());
        long current = solutionQueryRequest.getCurrent();
        long size = solutionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Solution> solutionPage = solutionService.page(new Page<>(current, size),
                solutionService.getQueryWrapper(solutionQueryRequest));
        return ResultUtils.success(solutionService.getSolutionVOPage(solutionPage, request));
    }


    /**
     * 编辑（用户）
     *
     * @param solutionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPost(@RequestBody SolutionEditRequest solutionEditRequest, HttpServletRequest request) {
        if (solutionEditRequest == null || solutionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Solution solution = new Solution();
        BeanUtils.copyProperties(solutionEditRequest, solution);
        List<String> tags = solutionEditRequest.getTags();
        if (tags != null) {
            solution.setTags(GSON.toJson(tags));
        }
        // 参数校验
        solutionService.validPost(solution, false);
        User loginUser = userService.getLoginUser(request);
        long id = solutionEditRequest.getId();
        // 判断是否存在
        Solution oldSolution = solutionService.getById(id);
        ThrowUtils.throwIf(oldSolution == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldSolution.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = solutionService.updateById(solution);
        return ResultUtils.success(result);
    }


}
