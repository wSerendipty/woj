package com.wcy.woj.model.vo;

import cn.hutool.json.JSONUtil;
import com.wcy.woj.model.dto.question.JudgeCase;
import com.wcy.woj.model.dto.question.JudgeConfig;
import com.wcy.woj.model.entity.Question;
import com.wcy.woj.model.entity.QuestionTemplate;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目封装类
 * @TableName question
 */
@Data
public class QuestionVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题解数
     */
    private Integer solutionNum;

    /**
     * 题目难度（简单、中等、困难）
     */
    private String difficulty;

    /**
     * 题目状态（0：未开始，1：通过 2：尝试过）
     */
    private Integer status;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * 测试判题用例（json 对象数组）
     */
    private List<JudgeCase> testJudgeCase;

    private List<QuestionTemplateVO> questionTemplates;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建题目人的信息
     */
    private UserVO userVO;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig voJudgeConfig = questionVO.getJudgeConfig();
        List<JudgeCase> testJudgeCase = questionVO.getTestJudgeCase();
        if (voJudgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        if (testJudgeCase != null) {
            question.setTestJudgeCase(JSONUtil.toJsonStr(testJudgeCase));
        }
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        List<String> tagList = JSONUtil.toList(question.getTags(), String.class);
        questionVO.setTags(tagList);
        String judgeConfigStr = question.getJudgeConfig();
        String testJudgeCase = question.getTestJudgeCase();
        questionVO.setTestJudgeCase(JSONUtil.toList(testJudgeCase, JudgeCase.class));
        questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfigStr, JudgeConfig.class));
        return questionVO;
    }

    private static final long serialVersionUID = 1L;
}