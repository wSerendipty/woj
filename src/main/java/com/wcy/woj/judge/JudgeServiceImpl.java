package com.wcy.woj.judge;

import cn.hutool.json.JSONUtil;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.judge.codesandbox.CodeSandbox;
import com.wcy.woj.judge.codesandbox.CodeSandboxFactory;
import com.wcy.woj.judge.codesandbox.CodeSandboxProxy;
import com.wcy.woj.judge.codesandbox.model.ExecuteCodeRequest;
import com.wcy.woj.judge.codesandbox.model.ExecuteCodeResponse;
import com.wcy.woj.judge.codesandbox.model.ExecuteStatusEnum;
import com.wcy.woj.judge.model.JudgeInfo;
import com.wcy.woj.judge.strategy.JudgeContext;
import com.wcy.woj.model.dto.question.JudgeCase;
import com.wcy.woj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.wcy.woj.model.entity.Question;
import com.wcy.woj.model.entity.QuestionSubmit;
import com.wcy.woj.model.enums.JudgeInfoMessageEnum;
import com.wcy.woj.model.enums.QuestionSubmitStatusEnum;
import com.wcy.woj.model.vo.QuestionSubmitVO;
import com.wcy.woj.service.QuestionService;
import com.wcy.woj.service.QuestionSubmitService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        if (executeCodeResponse == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限执行沙箱");
        }
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        Integer status = executeCodeResponse.getStatus();
        JudgeContext judgeContext = new JudgeContext();
        JudgeInfo executeJudgeInfo = executeCodeResponse.getJudgeInfo();
        if (Objects.equals(ExecuteStatusEnum.getEnumByCode(status), ExecuteStatusEnum.COMPILE_ERROR)) {
            // 编译错误
            executeJudgeInfo.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            //修改数据库中的判题结果
            questionSubmitUpdate = updateQuestionSubmitStatus(executeJudgeInfo, questionSubmitId);
            return questionSubmitUpdate;
        } else if (Objects.equals(ExecuteStatusEnum.getEnumByCode(status), ExecuteStatusEnum.RUNTIME_ERROR)) {
            // 运行时错误
            executeJudgeInfo.setMessage(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue());
            // 修改数据库中的判题结果
            questionSubmitUpdate = updateQuestionSubmitStatus(executeJudgeInfo, questionSubmitId);
            return questionSubmitUpdate;
        }
        judgeContext.setJudgeInfo(executeJudgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setLanguage(language);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 6）修改数据库中的判题结果
        questionSubmitUpdate = updateQuestionSubmitStatus(judgeInfo, questionSubmitId);
        return questionSubmitUpdate;

    }

    @Override
    public QuestionSubmitVO runJudge(QuestionSubmitAddRequest questionSubmitAddRequest) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        Long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmitAddRequest.getLanguage();
        String code = questionSubmitAddRequest.getCode();
        // 获取输入用例
        List<JudgeCase> judgeCaseList = questionSubmitAddRequest.getJudgeCase();
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        List<String> expectOutList = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        if (executeCodeResponse == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限执行沙箱");
        }
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        QuestionSubmitVO questionSubmit = new QuestionSubmitVO();
        Integer status = executeCodeResponse.getStatus();
        JudgeContext judgeContext = new JudgeContext();
        JudgeInfo executeJudgeInfo = executeCodeResponse.getJudgeInfo();
        if (Objects.equals(ExecuteStatusEnum.getEnumByCode(status), ExecuteStatusEnum.COMPILE_ERROR)) {
            // 编译错误
            executeJudgeInfo.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            return questionSubmit;
        } else if (Objects.equals(ExecuteStatusEnum.getEnumByCode(status), ExecuteStatusEnum.RUNTIME_ERROR)) {
            // 运行时错误
            executeJudgeInfo.setMessage(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue());
            return questionSubmit;
        }
        judgeContext.setJudgeInfo(executeJudgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setLanguage(language);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        judgeInfo.setInput(inputList);
        judgeInfo.setOutput(outputList);
        // todo 修改运行JudgeInfo的代码
        judgeInfo.setExpectedOutput(expectOutList);
        questionSubmit.setJudgeInfo(judgeInfo);
        return questionSubmit;
    }

    public QuestionSubmit updateQuestionSubmitStatus(JudgeInfo judgeInfo, long questionSubmitId) {
        // 6）修改数据库中的判题结果
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        return questionSubmitService.getById(questionSubmitId);
    }

}
