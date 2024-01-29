package com.wcy.woj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcy.woj.model.dto.post.PostQueryRequest;
import com.wcy.woj.model.dto.solution.SolutionQueryRequest;
import com.wcy.woj.model.entity.Post;
import com.wcy.woj.model.entity.Solution;
import com.wcy.woj.model.vo.SolutionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 王长远
* @description 针对表【solution(题目题解)】的数据库操作Service
* @createDate 2024-01-25 12:48:25
*/
public interface SolutionService extends IService<Solution> {

    /**
     * 校验
     *
     * @param solution
     * @param add
     */
    void validPost(Solution solution, boolean add);

    /**
     * 获取查询条件
     *
     * @param solutionQueryRequest
     * @return
     */
    QueryWrapper<Solution> getQueryWrapper(SolutionQueryRequest solutionQueryRequest);

    SolutionVO getSolutionVO(Solution solution, HttpServletRequest request);

    Page<SolutionVO> getSolutionVOPage(Page<Solution> solutionPage, HttpServletRequest request);
}
