package com.wcy.woj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wcy.woj.common.BaseResponse;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.common.ResultUtils;
import com.wcy.woj.exception.ThrowUtils;
import com.wcy.woj.model.dto.tag.TagAddRequest;
import com.wcy.woj.model.dto.tag.TagQueryRequest;
import com.wcy.woj.model.entity.Tag;
import com.wcy.woj.model.vo.TagVO;
import com.wcy.woj.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/8 9:57
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    @Resource
    private TagService tagService;

    @PostMapping("/add")
    public BaseResponse<String> addTag(@RequestBody TagAddRequest tagAddRequest) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagAddRequest, tag);
        boolean save = tagService.save(tag);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success("添加成功");
    }

    @PostMapping("/list")
    public BaseResponse<List<TagVO>> listTag(@RequestBody TagQueryRequest tagQueryRequest) {
        QueryWrapper<Tag> queryWrapper = tagService.getQueryWrapper(tagQueryRequest);
        return ResultUtils.success(tagService.listTagVO(queryWrapper));
    }

}
