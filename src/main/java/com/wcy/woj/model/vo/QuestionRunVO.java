package com.wcy.woj.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wcy.woj.judge.model.JudgeInfo;
import com.wcy.woj.model.dto.question.JudgeCase;
import com.wcy.woj.model.entity.QuestionRun;
import com.wcy.woj.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目运行表
 */
@Data
public class QuestionRunVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 测试判题用例（json 数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

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
     * 提交用户信息
     */
    private UserVO userVO;

    /**
     * 对应题目信息
     */
    private QuestionVO questionVO;

    /**
     * 包装类转对象
     *
     * @param questionRunVO
     * @return
     */
    public static QuestionRun voToObj(QuestionRunVO questionRunVO) {
        if (questionRunVO == null) {
            return null;
        }
        QuestionRun questionRun = new QuestionRun();
        BeanUtils.copyProperties(questionRunVO, questionRun);
        JudgeInfo judgeInfoObj = questionRunVO.getJudgeInfo();
        if (judgeInfoObj != null) {
            questionRun.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoObj));
        }
        List<JudgeCase> judgeCases = questionRunVO.getJudgeCase();
        if (judgeCases != null) {
            questionRun.setJudgeCase(JSONUtil.toJsonStr(judgeCases));
        }
        return questionRun;
    }

    /**
     * 对象转包装类
     *
     * @param questionRun
     * @return
     */
    public static QuestionRunVO objToVo(QuestionRun questionRun) {
        if (questionRun == null) {
            return null;
        }
        QuestionRunVO questionRunVO = new QuestionRunVO();
        BeanUtils.copyProperties(questionRun, questionRunVO);
        String judgeInfoStr = questionRun.getJudgeInfo();
        questionRunVO.setJudgeInfo(JSONUtil.toBean(judgeInfoStr, JudgeInfo.class));
        String judgeCases = questionRun.getJudgeCase();
        questionRunVO.setJudgeCase(JSONUtil.toList(judgeCases, JudgeCase.class));
        return questionRunVO;
    }

    private static final long serialVersionUID = 1L;
}