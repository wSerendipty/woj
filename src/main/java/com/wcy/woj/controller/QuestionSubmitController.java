package com.wcy.woj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wcy.woj.common.BaseResponse;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.common.ResultUtils;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.wcy.woj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.wcy.woj.model.entity.Question;
import com.wcy.woj.model.entity.QuestionSubmit;
import com.wcy.woj.model.entity.User;
import com.wcy.woj.model.vo.QuestionSubmitVO;
import com.wcy.woj.service.QuestionSubmitService;
import com.wcy.woj.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/17 10:09
 */
@RestController
@RequestMapping("/question_submit")
public class QuestionSubmitController {

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 提交记录的 id
     */
    @PostMapping("/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }



    /**
     * 分页获取题目提交列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }

    /**
     * 获取题目提交ID（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<QuestionSubmitVO> getQuestionSubmitById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionSubmit questionSubmit = questionSubmitService.getById(id);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVO(questionSubmit, loginUser));
    }

}
