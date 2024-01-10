package com.wcy.woj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcy.woj.mapper.TagMapper;
import com.wcy.woj.model.dto.tag.TagQueryRequest;
import com.wcy.woj.model.entity.Tag;
import com.wcy.woj.model.vo.TagVO;
import com.wcy.woj.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 王长远
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-01-08 09:47:20
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

    @Override
    public QueryWrapper<Tag> getQueryWrapper(TagQueryRequest tagQueryRequest) {
        Long id = tagQueryRequest.getId();
        String name = tagQueryRequest.getName();
        String belongType = tagQueryRequest.getBelongType();
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtils.isNotNull(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotNull(name), "name", name);
        queryWrapper.eq(ObjectUtils.isNotNull(belongType), "belongType", belongType);
        return queryWrapper;
    }

    @Override
    public List<TagVO> listTagVO(QueryWrapper<Tag> queryWrapper) {
        List<Tag> tagList = this.list(queryWrapper);
        if (ObjectUtils.isEmpty(tagList)) {
            return new ArrayList<>();
        }
        List<TagVO> tagVOList = tagList.stream().map(tag -> {
            TagVO tagVO = new TagVO();
            BeanUtils.copyProperties(tag, tagVO);
            return tagVO;
        }).collect(Collectors.toList());

        return tagVOList;
    }
}




