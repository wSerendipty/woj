package com.wcy.woj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcy.woj.model.dto.daily.DailyQueryRequest;
import com.wcy.woj.model.entity.Daily;

/**
* @author 王长远
* @description 针对表【daily(每日)】的数据库操作Service
* @createDate 2024-01-24 13:47:57
*/
public interface DailyService extends IService<Daily> {

    QueryWrapper<Daily> getQueryWrapper(DailyQueryRequest dailyQueryRequest);

}
