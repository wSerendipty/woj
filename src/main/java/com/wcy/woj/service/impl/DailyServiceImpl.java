package com.wcy.woj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcy.woj.mapper.DailyMapper;
import com.wcy.woj.model.dto.daily.DailyQueryRequest;
import com.wcy.woj.model.entity.Daily;
import com.wcy.woj.service.DailyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
* @author 王长远
* @description 针对表【daily(每日)】的数据库操作Service实现
* @createDate 2024-01-24 13:47:57
*/
@Service
public class DailyServiceImpl extends ServiceImpl<DailyMapper, Daily>
    implements DailyService{


    @Override
    public QueryWrapper<Daily> getQueryWrapper(DailyQueryRequest dailyQueryRequest) {

        Long id = dailyQueryRequest.getId();
        Long questionId = dailyQueryRequest.getQuestionId();
        Long postId = dailyQueryRequest.getPostId();
        String belongType = dailyQueryRequest.getBelongType();
        // 获取今天的开始和结束时间（包括整个24小时）
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 今天0点
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay(); // 明天0点，实际上代表今天23:59:59
        // 转换为Java.util.Date类型，取决于数据库字段的实际类型
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).minusNanos(1).toInstant()); // 减去一纳秒以确保不包含第二天
        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtils.isNotNull(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotNull(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotNull(postId), "postId", postId);
        queryWrapper.eq(ObjectUtils.isNotNull(belongType), "belongType", belongType);
        queryWrapper.between("createTime", startDate, endDate);
        return queryWrapper;
    }
}




