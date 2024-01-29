package com.wcy.woj.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.constant.CommonConstant;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.judge.JudgeService;
import com.wcy.woj.mapper.QuestionRunMapper;
import com.wcy.woj.model.dto.questionrun.QuestionRunAddRequest;
import com.wcy.woj.model.dto.questionrun.QuestionRunQueryRequest;
import com.wcy.woj.model.entity.Question;
import com.wcy.woj.model.entity.QuestionRun;
import com.wcy.woj.model.entity.QuestionSubmit;
import com.wcy.woj.model.entity.User;
import com.wcy.woj.model.enums.QuestionSubmitLanguageEnum;
import com.wcy.woj.model.enums.QuestionSubmitStatusEnum;
import com.wcy.woj.model.vo.QuestionRunVO;
import com.wcy.woj.model.vo.QuestionSubmitVO;
import com.wcy.woj.service.QuestionRunService;
import com.wcy.woj.service.QuestionService;
import com.wcy.woj.service.UserService;
import com.wcy.woj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author 王长远
* @description 针对表【question_run(题目运行表)】的数据库操作Service实现
* @createDate 2024-01-19 12:38:04
*/
@Service
public class QuestionRunServiceImpl extends ServiceImpl<QuestionRunMapper, QuestionRun>
    implements QuestionRunService {

    @Resource
    private QuestionService questionService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    @Resource
    private UserService userService;

    @Override
    public Long doQuestionRun(QuestionRunAddRequest questionRunAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionRunAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        // 校验是否有代码
        String code = questionRunAddRequest.getCode();
        if (StringUtils.isBlank(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码不能为空");
        }
        long questionId = questionRunAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        QuestionRun questionRun = new QuestionRun();
        // 每个用户串行提交题目
        questionRun.setUserId(userId);
        questionRun.setQuestionId(questionId);
        questionRun.setCode(questionRunAddRequest.getCode());
        questionRun.setLanguage(language);
        questionRun.setJudgeCase(JSONUtil.toJsonStr(questionRunAddRequest.getJudgeCase()));
        // 设置初始状态
        questionRun.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionRun.setJudgeInfo("{}");
        boolean save = this.save(questionRun);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionRun.getId();
        // 执行判题服务
        CompletableFuture.runAsync(() -> {
            judgeService.runJudge(questionSubmitId,questionRunAddRequest);
        });
        return questionSubmitId;
    }

    @Override
    public QueryWrapper<QuestionRun> getQueryWrapper(QuestionRunQueryRequest questionRunQueryRequest) {
        QueryWrapper<QuestionRun> queryWrapper = new QueryWrapper<>();
        if (questionRunQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionRunQueryRequest.getLanguage();
        Integer status = questionRunQueryRequest.getStatus();
        Long questionId = questionRunQueryRequest.getQuestionId();
        Long userId = questionRunQueryRequest.getUserId();
        String sortField = questionRunQueryRequest.getSortField();
        String sortOrder = questionRunQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionRunVO getQuestionRunVO(QuestionRun questionRun, User loginUser) {
        QuestionRunVO questionRunVO = QuestionRunVO.objToVo(questionRun);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionRun.getUserId() && !userService.isAdmin(loginUser)) {
            questionRunVO.setCode(null);
        }
        return questionRunVO;
    }

    @Override
    public Page<QuestionRunVO> getQuestionRunVOPage(Page<QuestionRun> questionRunPage, User loginUser) {
        List<QuestionRun> questionRunList = questionRunPage.getRecords();
        Page<QuestionRunVO> questionRunVOPage = new Page<>(questionRunPage.getCurrent(), questionRunPage.getSize(), questionRunPage.getTotal());
        if (CollectionUtils.isEmpty(questionRunList)) {
            return questionRunVOPage;
        }
        List<QuestionRunVO> questionRunVOList = questionRunList.stream()
                .map(questionRun -> getQuestionRunVO(questionRun, loginUser))
                .collect(Collectors.toList());
        questionRunVOPage.setRecords(questionRunVOList);
        return questionRunVOPage;
    }
}




