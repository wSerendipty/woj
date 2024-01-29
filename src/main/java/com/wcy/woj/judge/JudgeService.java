package com.wcy.woj.judge;

import com.wcy.woj.model.dto.questionrun.QuestionRunAddRequest;
import com.wcy.woj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.wcy.woj.model.entity.QuestionRun;
import com.wcy.woj.model.entity.QuestionSubmit;
import com.wcy.woj.model.vo.QuestionRunVO;
import com.wcy.woj.model.vo.QuestionSubmitVO;

/**
 * 判题服务
 */
public interface JudgeService {

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId,long userId, QuestionSubmitAddRequest questionSubmitAddRequest);


    QuestionRun runJudge(long questionRunId,QuestionRunAddRequest questionRunAddRequest);


}
