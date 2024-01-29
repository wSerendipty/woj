package com.wcy.woj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcy.woj.model.dto.question.QuestionQueryRequest;
import com.wcy.woj.model.dto.questionTemplate.QuestionTemplateQueryRequest;
import com.wcy.woj.model.entity.Question;
import com.wcy.woj.model.entity.QuestionTemplate;

import java.util.List;

/**
* @author 王长远
* @description 针对表【question_template(题目提交)】的数据库操作Service
* @createDate 2024-01-22 09:18:36
*/
public interface QuestionTemplateService extends IService<QuestionTemplate> {

    /**
     * 校验
     *
     * @param questionTemplate
     * @param add
     */
    void validQuestion(QuestionTemplate questionTemplate, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionTemplateQueryRequest
     * @return
     */
    QueryWrapper<QuestionTemplate> getQueryWrapper(QuestionTemplateQueryRequest questionTemplateQueryRequest);

    List<QuestionTemplate> listByQuestionId(Long questionId);

}
