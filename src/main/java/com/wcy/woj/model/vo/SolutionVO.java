package com.wcy.woj.model.vo;

import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.wcy.woj.model.entity.Post;
import com.wcy.woj.model.entity.Solution;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 */
@Data
public class SolutionVO implements Serializable {

    private final static Gson GSON = new Gson();

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
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 特殊标签列表
     */
    private List<String> specialTagList;

    /**
     * 创建人信息
     */
    private UserVO user;


    /**
     * 包装类转对象
     *
     * @param solutionVO
     * @return
     */
    public static Solution voToObj(SolutionVO solutionVO) {
        if (solutionVO == null) {
            return null;
        }
        Solution solution = new Solution();
        BeanUtils.copyProperties(solutionVO, solution);
        List<String> tagList = solutionVO.getTagList();
        List<String> specialTagList = solutionVO.getSpecialTagList();
        if (tagList != null) {
            solution.setTags(JSONUtil.toJsonStr(tagList));
        }
        if (specialTagList != null) {
            solution.setSpecialTags(JSONUtil.toJsonStr(specialTagList));
        }
        return solution;
    }

    /**
     * 对象转包装类
     *
     * @param solution
     * @return
     */
    public static SolutionVO objToVo(Solution solution) {
        if (solution == null) {
            return null;
        }
        SolutionVO solutionVO = new SolutionVO();
        BeanUtils.copyProperties(solution, solutionVO);
        solutionVO.setTagList(JSONUtil.toList(solution.getTags(), String.class));
        solutionVO.setSpecialTagList(JSONUtil.toList(solution.getSpecialTags(), String.class));
        return solutionVO;
    }
}
