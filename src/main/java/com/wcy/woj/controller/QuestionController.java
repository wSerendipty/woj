package com.wcy.woj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.wcy.woj.model.dto.daily.DailyQueryRequest;
import com.wcy.woj.model.dto.question.*;
import com.wcy.woj.model.dto.questionStatus.QuestionStatusQueryRequest;
import com.wcy.woj.model.entity.Question;
import com.wcy.woj.model.entity.QuestionStatus;
import com.wcy.woj.model.entity.QuestionTemplate;
import com.wcy.woj.model.entity.User;
import com.wcy.woj.model.vo.QuestionFinishVO;
import com.wcy.woj.model.vo.QuestionTemplateVO;
import com.wcy.woj.model.vo.QuestionVO;
import com.wcy.woj.service.QuestionService;
import com.wcy.woj.service.QuestionStatusService;
import com.wcy.woj.service.QuestionTemplateService;
import com.wcy.woj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionTemplateService questionTemplateService;

    @Resource
    private QuestionStatusService questionStatusService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        List<JudgeCase> testJudgeCase = questionAddRequest.getTestJudgeCase();
        if (testJudgeCase != null) {
            question.setTestJudgeCase(GSON.toJson(testJudgeCase));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, true);
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        // 保存模板
        List<QuestionTemplate> questionTemplates = questionAddRequest.getQuestionTemplates();
        if (questionTemplates != null) {
            for (QuestionTemplate questionTemplate : questionTemplates) {
                questionTemplate.setQuestionId(newQuestionId);
                questionTemplate.setUserId(loginUser.getId());
                boolean save = questionTemplateService.save(questionTemplate);
                ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
            }
        }
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除 （仅管理员）
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean b = questionService.removeById(id);
        // 删除模板
        QueryWrapper<QuestionTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("questionId", id);
        boolean remove = questionTemplateService.remove(queryWrapper);
        return ResultUtils.success(b && remove);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取（脱敏）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question,"normal", request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 根据 id 获取（管理员）
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 1. 查询模板
        List<QuestionTemplate> questionTemplates = questionTemplateService.listByQuestionId(question.getId());
        List<QuestionTemplateVO> questionTemplateVOList = questionTemplates.stream().map(questionTemplate -> {
            QuestionTemplateVO questionTemplateVO = new QuestionTemplateVO();
            questionTemplateVO.setCode(questionTemplate.getCode());
            questionTemplateVO.setLanguage(questionTemplate.getLanguage());
            return questionTemplateVO;
        }).collect(Collectors.toList());
        question.setQuestionTemplates(questionTemplateVOList);
        // 2. 查询题目状态
        QuestionStatus questionStatus = questionStatusService.getByQuestionIdAndUserIdAndType(question.getId(), userService.getLoginUser(request).getId(), "normal");
        if (questionStatus == null) {
            question.setStatus(0);
        }else {
            question.setStatus(questionStatus.getStatus());
        }
        return ResultUtils.success(question);
    }

    /**
     * 分页获取题目列表（仅管理员）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                           HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        List<Question> questionList = questionPage.getRecords();
        // 填充信息
        List<Question> questions = questionList.stream().map(question -> {
            List<QuestionTemplate> questionTemplates = questionTemplateService.listByQuestionId(question.getId());
            List<QuestionTemplateVO> questionTemplateVOList = questionTemplates.stream().map(questionTemplate -> {
                QuestionTemplateVO questionTemplateVO = new QuestionTemplateVO();
                questionTemplateVO.setCode(questionTemplate.getCode());
                questionTemplateVO.setLanguage(questionTemplate.getLanguage());
                return questionTemplateVO;
            }).collect(Collectors.toList());
            question.setQuestionTemplates(questionTemplateVOList);
            // 2. 查询题目状态
            QuestionStatus questionStatus = questionStatusService.getByQuestionIdAndUserIdAndType(question.getId(), userService.getLoginUser(request).getId(), "normal");
            if (questionStatus == null) {
                question.setStatus(0);
            }else {
                question.setStatus(questionStatus.getStatus());
            }
            return question;
        }).collect(Collectors.toList());
        questionPage.setRecords(questions);
        return ResultUtils.success(questionPage);
    }

    /**
     * 随机每日一题
     */
    @PostMapping("/daily")
    public BaseResponse<QuestionVO> getDailyQuestion(@RequestBody DailyQueryRequest dailyQueryRequest, HttpServletRequest request) {
        QuestionVO dailyQuestionVO = questionService.getDailyQuestionVO(dailyQueryRequest, request);
        return ResultUtils.success(dailyQuestionVO);
    }

    /**
     * 获取题目完成情况
     */
    @PostMapping("/finish")
    public BaseResponse<QuestionFinishVO> getQuestionFinish(@RequestBody QuestionStatusQueryRequest questionStatusQueryRequest, HttpServletRequest request) {
        QuestionFinishVO questionFinishVO = questionStatusService.getQuestionFinish(questionStatusQueryRequest, request);
        return ResultUtils.success(questionFinishVO);
    }


}
