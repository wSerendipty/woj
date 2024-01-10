package com.wcy.woj.judge;

import com.wcy.woj.judge.codesandbox.model.JudgeInfo;
import com.wcy.woj.judge.strategy.DefaultJudgeStrategy;
import com.wcy.woj.judge.strategy.JavaLanguageJudgeStrategy;
import com.wcy.woj.judge.strategy.JudgeContext;
import com.wcy.woj.judge.strategy.JudgeStrategy;
import com.wcy.woj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
