package com.wcy.woj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcy.woj.model.dto.question.QuestionQueryRequest;
import com.wcy.woj.model.dto.questionStatus.QuestionStatusQueryRequest;
import com.wcy.woj.model.entity.Question;
import com.wcy.woj.model.entity.QuestionStatus;
import com.wcy.woj.model.vo.QuestionFinishVO;
import com.wcy.woj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 王长远
* @description 针对表【question_status(题目状态表)】的数据库操作Service
* @createDate 2024-01-27 17:37:17
*/
public interface QuestionStatusService extends IService<QuestionStatus> {

    QuestionStatus getByQuestionIdAndUserIdAndType(Long questionId,long userId, String type);


    /**
     * 获取查询条件
     *
     * @param questionStatusQueryRequest
     * @return
     */
    QueryWrapper<QuestionStatus> getQueryWrapper(QuestionStatusQueryRequest questionStatusQueryRequest);

    /**
     * 获取题目完成状态封装
     *
     * @param questionStatusQueryRequest
     * @param request
     * @return
     */
    QuestionFinishVO getQuestionFinish(QuestionStatusQueryRequest questionStatusQueryRequest, HttpServletRequest request);




}
