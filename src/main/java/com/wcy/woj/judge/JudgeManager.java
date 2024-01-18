package com.wcy.woj.judge;

import com.wcy.woj.judge.model.JudgeInfo;
import com.wcy.woj.judge.strategy.DefaultJudgeStrategy;
import com.wcy.woj.judge.strategy.JavaLanguageJudgeStrategy;
import com.wcy.woj.judge.strategy.JudgeContext;
import com.wcy.woj.judge.strategy.JudgeStrategy;
import com.wcy.woj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.wcy.woj.model.enums.QuestionSubmitLanguageEnum;
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
        String language = judgeContext.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (QuestionSubmitLanguageEnum.getEnumByValue(language) == QuestionSubmitLanguageEnum.JAVA) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
