package com.wcy.woj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wcy.woj.common.BaseResponse;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.common.ResultUtils;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.model.dto.questionrun.QuestionRunAddRequest;
import com.wcy.woj.model.dto.questionrun.QuestionRunQueryRequest;
import com.wcy.woj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.wcy.woj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.wcy.woj.model.entity.QuestionRun;
import com.wcy.woj.model.entity.QuestionSubmit;
import com.wcy.woj.model.entity.User;
import com.wcy.woj.model.vo.QuestionRunVO;
import com.wcy.woj.model.vo.QuestionSubmitVO;
import com.wcy.woj.service.QuestionRunService;
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
@RequestMapping("/question_run")
public class QuestionRunController {

    @Resource
    private UserService userService;

    @Resource
    private QuestionRunService questionRunService;

    /**
     * 运行题目
     *
     * @param questionRunAddRequest
     * @param request
     * @return 运行记录的 id
     */
    @PostMapping("/run")
    public BaseResponse<Long> doQuestionRun(@RequestBody QuestionRunAddRequest questionRunAddRequest,
                                                     HttpServletRequest request) {
        if (questionRunAddRequest == null || questionRunAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final User loginUser = userService.getLoginUser(request);
        Long questionRunId = questionRunService.doQuestionRun(questionRunAddRequest, loginUser);
        return ResultUtils.success(questionRunId);
    }

    /**
     * 分页获取题目运行列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param questionRunQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionRunVO>> listQuestionRunByPage(@RequestBody QuestionRunQueryRequest questionRunQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionRunQueryRequest.getCurrent();
        long size = questionRunQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionRun> questionRunPage = questionRunService.page(new Page<>(current, size),
                questionRunService.getQueryWrapper(questionRunQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionRunService.getQuestionRunVOPage(questionRunPage, loginUser));
    }

    /**
     * 获取题目运行ByID（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<QuestionRunVO> getQuestionRunById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionRun questionRun = questionRunService.getById(id);
        if (questionRun == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionRunService.getQuestionRunVO(questionRun, loginUser));
    }

}
