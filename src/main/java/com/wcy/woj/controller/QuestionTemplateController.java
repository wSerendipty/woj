package com.wcy.woj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wcy.woj.annotation.AuthCheck;
import com.wcy.woj.common.BaseResponse;
import com.wcy.woj.common.DeleteRequest;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.common.ResultUtils;
import com.wcy.woj.constant.UserConstant;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.exception.ThrowUtils;
import com.wcy.woj.model.dto.questionTemplate.QuestionTemplateQueryRequest;
import com.wcy.woj.model.entity.QuestionTemplate;
import com.wcy.woj.service.QuestionTemplateService;
import com.wcy.woj.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/8 9:57
 */
@RestController
@RequestMapping("/question/template")
public class QuestionTemplateController {
    @Resource
    private QuestionTemplateService questionTemplateService;
    @Resource
    private UserService userService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestionTemplate(@RequestBody QuestionTemplate questionTemplate, HttpServletRequest request) {
        if (questionTemplate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        questionTemplate.setUserId(userService.getLoginUser(request).getId());
        questionTemplateService.validQuestion(questionTemplate, true);
        boolean b = questionTemplateService.save(questionTemplate);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(questionTemplate.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> deleteQuestionTemplate(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = questionTemplateService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success("删除成功");
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> updateQuestionTemplate(@RequestBody QuestionTemplate questionTemplate) {
        if (questionTemplate == null || questionTemplate.getId() == null || questionTemplate.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        questionTemplateService.validQuestion(questionTemplate, false);
        boolean b = questionTemplateService.updateById(questionTemplate);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success("更新成功");
    }

    @PostMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionTemplate>> listQuestionTemplate(@RequestBody QuestionTemplateQueryRequest questionTemplateQueryRequest) {
        if (questionTemplateQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionTemplateQueryRequest.getCurrent();
        long size = questionTemplateQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionTemplate> page = questionTemplateService.page(new Page<>(current, size),
                questionTemplateService.getQueryWrapper(questionTemplateQueryRequest));
        return ResultUtils.success(page);
    }
}
