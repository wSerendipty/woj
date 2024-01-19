package com.wcy.woj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcy.woj.model.dto.questionrun.QuestionRunAddRequest;
import com.wcy.woj.model.dto.questionrun.QuestionRunQueryRequest;
import com.wcy.woj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.wcy.woj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.wcy.woj.model.entity.QuestionRun;
import com.wcy.woj.model.entity.QuestionSubmit;
import com.wcy.woj.model.entity.User;
import com.wcy.woj.model.vo.QuestionRunVO;
import com.wcy.woj.model.vo.QuestionSubmitVO;

/**
* @author 王长远
* @description 针对表【question_run(题目运行表)】的数据库操作Service
* @createDate 2024-01-19 12:38:04
*/
public interface QuestionRunService extends IService<QuestionRun> {
    /**
     * 题目运行
     *
     * @param questionRunAddRequest 题目运行信息
     * @param loginUser
     * @return
     */
    Long doQuestionRun(QuestionRunAddRequest questionRunAddRequest, User loginUser);




    /**
     * 获取查询条件
     *
     * @param questionRunQueryRequest
     * @return
     */
    QueryWrapper<QuestionRun> getQueryWrapper(QuestionRunQueryRequest questionRunQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionRun
     * @param loginUser
     * @return
     */
    QuestionRunVO getQuestionRunVO(QuestionRun questionRun, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionRunPage
     * @param loginUser
     * @return
     */
    Page<QuestionRunVO> getQuestionRunVOPage(Page<QuestionRun> questionRunPage, User loginUser);
}
