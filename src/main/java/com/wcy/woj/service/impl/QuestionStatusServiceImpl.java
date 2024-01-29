package com.wcy.woj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcy.woj.mapper.QuestionStatusMapper;
import com.wcy.woj.model.dto.questionStatus.QuestionStatusQueryRequest;
import com.wcy.woj.model.entity.Question;
import com.wcy.woj.model.entity.QuestionStatus;
import com.wcy.woj.model.vo.QuestionFinishVO;
import com.wcy.woj.service.QuestionService;
import com.wcy.woj.service.QuestionStatusService;
import com.wcy.woj.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 王长远
* @description 针对表【question_status(题目状态表)】的数据库操作Service实现
* @createDate 2024-01-27 17:37:17
*/
@Service
public class QuestionStatusServiceImpl extends ServiceImpl<QuestionStatusMapper, QuestionStatus>
    implements QuestionStatusService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private QuestionService questionService;

    @Override
    public QuestionStatus getByQuestionIdAndUserIdAndType(Long questionId,long userId, String type) {
        QueryWrapper<QuestionStatus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("questionId", questionId);
        queryWrapper.eq("type", type);
        // 1. 查询题目状态
        return this.getOne(queryWrapper);
    }

    @Override
    public QueryWrapper<QuestionStatus> getQueryWrapper(QuestionStatusQueryRequest questionStatusQueryRequest) {
        QueryWrapper<QuestionStatus> queryWrapper = new QueryWrapper<>();
        if (questionStatusQueryRequest == null) {
            return queryWrapper;
        }
        Long questionId = questionStatusQueryRequest.getQuestionId();
        String type = questionStatusQueryRequest.getType();
        Long userId = questionStatusQueryRequest.getUserId();
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(StringUtils.isNotBlank(type), "type", type);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        return queryWrapper;
    }

    @Override
    public QuestionFinishVO getQuestionFinish(QuestionStatusQueryRequest questionStatusQueryRequest, HttpServletRequest request) {
        questionStatusQueryRequest.setUserId(userService.getLoginUser(request).getId());
        QueryWrapper<QuestionStatus> queryWrapper = getQueryWrapper(questionStatusQueryRequest);
        List<QuestionStatus> questionStatusList = this.list(queryWrapper);
        QuestionFinishVO questionFinishVO = new QuestionFinishVO();
        // 1. 获取所有题目的数量
        long totalNum = questionService.count();
        int easyNum = 0;
        int mediumNum = 0;
        int hardNum = 0;
        int acNum = 0;

        // 2. 遍历questionStatusList 累加这些数量
        for (QuestionStatus questionStatus : questionStatusList) {
            if (questionStatus.getStatus().equals(1)){
                acNum++;
            }
            Question question = questionService.getById(questionStatus.getQuestionId());
            if (question == null) {
                continue;
            }
            switch (question.getDifficulty()) {
                case "简单":
                    easyNum++;
                    break;
                case "中等":
                    mediumNum++;
                    break;
                case "困难":
                    hardNum++;
                    break;
            }
        }

        // 3. 封装
        questionFinishVO.setAcNum(acNum);
        questionFinishVO.setEasyNum(easyNum);
        questionFinishVO.setMediumNum(mediumNum);
        questionFinishVO.setHardNum(hardNum);
        questionFinishVO.setTotalNum(totalNum);

        // 4. 查询所有的题目完成状态
        questionStatusQueryRequest.setType(null);
        queryWrapper = getQueryWrapper(questionStatusQueryRequest);
        List<QuestionStatus> questionStatusListAll = this.list(queryWrapper);
        questionFinishVO.setQuestionStatusVOList(questionStatusListAll);

        return questionFinishVO;
    }
}




