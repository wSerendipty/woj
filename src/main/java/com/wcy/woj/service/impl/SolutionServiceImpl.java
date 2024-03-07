package com.wcy.woj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.constant.CommonConstant;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.exception.ThrowUtils;
import com.wcy.woj.mapper.SolutionMapper;
import com.wcy.woj.model.dto.solution.SolutionQueryRequest;
import com.wcy.woj.model.entity.*;
import com.wcy.woj.model.vo.PostVO;
import com.wcy.woj.model.vo.SolutionVO;
import com.wcy.woj.model.vo.UserVO;
import com.wcy.woj.service.SolutionService;
import com.wcy.woj.service.UserService;
import com.wcy.woj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 王长远
* @description 针对表【solution(题目题解)】的数据库操作Service实现
* @createDate 2024-01-25 12:48:25
*/
@Service
public class SolutionServiceImpl extends ServiceImpl<SolutionMapper, Solution>
    implements SolutionService {

    @Resource
    private UserService userService;

    @Override
    public void validPost(Solution solution, boolean add) {
        if (solution == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = solution.getTitle();
        String content = solution.getContent();
        String tags = solution.getTags();
        Long questionId = solution.getQuestionId();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(questionId), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    @Override
    public QueryWrapper<Solution> getQueryWrapper(SolutionQueryRequest solutionQueryRequest) {
        QueryWrapper<Solution> queryWrapper = new QueryWrapper<>();
        if (solutionQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = solutionQueryRequest.getSortField();
        String sortOrder = solutionQueryRequest.getSortOrder();
        Long id = solutionQueryRequest.getId();
        String title = solutionQueryRequest.getTitle();
        String content = solutionQueryRequest.getContent();
        List<String> tagList = solutionQueryRequest.getTags();
        Long userId = solutionQueryRequest.getUserId();
        Long questionId = solutionQueryRequest.getQuestionId();
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollectionUtils.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(ObjectUtils.isEmpty(sortField), false, "createTime");
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public SolutionVO getSolutionVO(Solution solution, HttpServletRequest request) {
        SolutionVO solutionVO = SolutionVO.objToVo(solution);
        // 1. 关联查询用户信息
        Long userId = solution.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        solutionVO.setUser(userVO);
        return solutionVO;
    }

    @Override
    public Page<SolutionVO> getSolutionVOPage(Page<Solution> solutionPage, HttpServletRequest request) {
        List<Solution> solutionList = solutionPage.getRecords();
        Page<SolutionVO> solutionVOPage = new Page<>(solutionPage.getCurrent(), solutionPage.getSize(), solutionPage.getTotal());
        if (CollectionUtils.isEmpty(solutionList)) {
            return solutionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = solutionList.stream().map(Solution::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<SolutionVO> solutionVOList = solutionList.stream().map(solution -> {
            SolutionVO solutionVO = SolutionVO.objToVo(solution);
            Long userId = solution.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            solutionVO.setUser(userService.getUserVO(user));
            return solutionVO;
        }).collect(Collectors.toList());
        solutionVOPage.setRecords(solutionVOList);
        return solutionVOPage;
    }
}




