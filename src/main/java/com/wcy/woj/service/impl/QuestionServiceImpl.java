package com.wcy.woj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.constant.CommonConstant;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.exception.ThrowUtils;
import com.wcy.woj.mapper.QuestionMapper;
import com.wcy.woj.model.dto.daily.DailyQueryRequest;
import com.wcy.woj.model.dto.question.QuestionQueryRequest;
import com.wcy.woj.model.entity.*;
import com.wcy.woj.model.vo.QuestionTemplateVO;
import com.wcy.woj.model.vo.QuestionVO;
import com.wcy.woj.model.vo.UserVO;
import com.wcy.woj.service.*;
import com.wcy.woj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 王长远
* @description 针对表【question(题目)】的数据库操作Service实现
* 
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService {


    @Resource
    private UserService userService;

    @Resource
    private QuestionTemplateService questionTemplateService;

    @Resource
    private DailyService dailyService;

    @Resource
    private QuestionStatusService questionStatusService;

    /**
     * 校验题目是否合法
     * @param question
     * @param add
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String testJudgeCase = question.getTestJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags,judgeCase,testJudgeCase), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        Long userId = questionQueryRequest.getUserId();
        String difficulty = questionQueryRequest.getDifficulty();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(difficulty), "difficulty", difficulty);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目详情 VO
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question,String type, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 查询相关模板
        List<QuestionTemplate> questionTemplates = questionTemplateService.listByQuestionId(question.getId());
        List<QuestionTemplateVO> questionTemplateVOList = questionTemplates.stream().map(questionTemplate -> {
            QuestionTemplateVO questionTemplateVO = new QuestionTemplateVO();
            questionTemplateVO.setCode(questionTemplate.getCode());
            questionTemplateVO.setLanguage(questionTemplate.getLanguage());
            return questionTemplateVO;
        }).collect(Collectors.toList());
        questionVO.setQuestionTemplates(questionTemplateVOList);
        // 2. 查询题目状态
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        if (loginUserPermitNull == null) {
            questionVO.setStatus(0);
        }else {
            QuestionStatus questionStatus = questionStatusService.getByQuestionIdAndUserIdAndType(question.getId(), loginUserPermitNull.getId(), type);
            if (questionStatus == null) {
                questionVO.setStatus(0);
            }else {
                questionVO.setStatus(questionStatus.getStatus());
            }
        }

        return questionVO;
    }

    /**
     * 获取题目列表 VO
     * @param questionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            // 1. 查询相关模板
            List<QuestionTemplate> questionTemplates = questionTemplateService.listByQuestionId(question.getId());
            List<QuestionTemplateVO> questionTemplateVOList = questionTemplates.stream().map(questionTemplate -> {
                QuestionTemplateVO questionTemplateVO = new QuestionTemplateVO();
                questionTemplateVO.setCode(questionTemplate.getCode());
                questionTemplateVO.setLanguage(questionTemplate.getLanguage());
                return questionTemplateVO;
            }).collect(Collectors.toList());
            questionVO.setQuestionTemplates(questionTemplateVOList);
            // 2. 查询题目状
            User loginUserPermitNull = userService.getLoginUserPermitNull(request);
            if (loginUserPermitNull == null) {
                questionVO.setStatus(0);
            }else {
                QuestionStatus questionStatus = questionStatusService.getByQuestionIdAndUserIdAndType(question.getId(), loginUserPermitNull.getId(), "normal");
                if (questionStatus == null) {
                    questionVO.setStatus(0);
                }else {
                    questionVO.setStatus(questionStatus.getStatus());
                }
            }
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }


    /**
     * 获取题目详情 VO（管理员）
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVOAdmin(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 查询相关模板
        List<QuestionTemplate> questionTemplates = questionTemplateService.listByQuestionId(question.getId());
        List<QuestionTemplateVO> questionTemplateVOList = questionTemplates.stream().map(questionTemplate -> {
            QuestionTemplateVO questionTemplateVO = new QuestionTemplateVO();
            questionTemplateVO.setCode(questionTemplate.getCode());
            questionTemplateVO.setLanguage(questionTemplate.getLanguage());
            return questionTemplateVO;
        }).collect(Collectors.toList());
        questionVO.setQuestionTemplates(questionTemplateVOList);
        // 2. 查询题目状态
        User loginUserPermitNull = userService.getLoginUserPermitNull(request);
        if (loginUserPermitNull == null) {
            questionVO.setStatus(0);
        }else {
            QuestionStatus questionStatus = questionStatusService.getByQuestionIdAndUserIdAndType(question.getId(), loginUserPermitNull.getId(), "normal");
            if (questionStatus == null) {
                questionVO.setStatus(0);
            }else {
                questionVO.setStatus(questionStatus.getStatus());
            }
        }

        // 3. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public QuestionVO getDailyQuestionVO(DailyQueryRequest dailyQueryRequest, HttpServletRequest request) {
        QueryWrapper<Daily> dailyQueryWrapper = dailyService.getQueryWrapper(dailyQueryRequest);
        Daily dailyServiceOne = dailyService.getOne(dailyQueryWrapper);
        Question question = this.getById(dailyServiceOne.getQuestionId());
        return getQuestionVO(question,"daily", request);
    }


}




