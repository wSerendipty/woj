package com.wcy.woj.judge.model;

import lombok.Data;

import java.util.List;

/**
 * 判题信息
 */
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 执行状态
     */
    private String status;

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 消耗时间（KB）
     */
    private Long time;

    /**
     * 实际输出
     */
    private List<String> output;

    /**
     * 期望输出
     */
    private List<String> expectedOutput;

    /**
     * 输入
     */
    private List<String> input;



}
