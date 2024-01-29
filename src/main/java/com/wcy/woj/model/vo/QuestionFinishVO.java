package com.wcy.woj.model.vo;

import com.wcy.woj.model.entity.QuestionStatus;
import lombok.Data;

import java.util.List;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/27 20:40
 */
@Data
public class QuestionFinishVO {
    private List<QuestionStatus> questionStatusVOList;
    private Integer easyNum;
    private Integer mediumNum;
    private Integer hardNum;

    private Integer acNum;
    private Long totalNum;
}
