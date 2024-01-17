package com.wcy.woj.judge.strategy;

import com.wcy.woj.judge.model.JudgeInfo;
import com.wcy.woj.model.dto.question.JudgeCase;
import com.wcy.woj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.wcy.woj.model.entity.Question;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmitAddRequest questionSubmit;

}
