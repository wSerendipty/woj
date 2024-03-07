package com.wcy.woj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wcy.woj.common.BaseResponse;
import com.wcy.woj.common.DeleteRequest;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.common.ResultUtils;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.exception.ThrowUtils;
import com.wcy.woj.model.dto.tag.TagAddRequest;
import com.wcy.woj.model.dto.tag.TagQueryRequest;
import com.wcy.woj.model.entity.Tag;
import com.wcy.woj.model.vo.TagVO;
import com.wcy.woj.service.TagService;
import org.apache.commons.lang3.ObjectUtils;
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
@RequestMapping("/post/tag")
public class TagController {
    @Resource
    private TagService tagService;

    @PostMapping("/add")
    public BaseResponse<String> addTag(@RequestBody TagAddRequest tagAddRequest) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagAddRequest, tag);
        // 判断标签名是否存在
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", tag.getName());
        queryWrapper.eq("belongType",tagAddRequest.getBelongType());
        Tag one = tagService.getOne(queryWrapper);
        ThrowUtils.throwIf(one != null, ErrorCode.REPEAT_ERROR);
        boolean save = tagService.save(tag);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success("添加成功");
    }

    @PostMapping("/list")
    public BaseResponse<List<TagVO>> listTag(@RequestBody TagQueryRequest tagQueryRequest) {
        QueryWrapper<Tag> queryWrapper = tagService.getQueryWrapper(tagQueryRequest);
        return ResultUtils.success(tagService.listTagVO(queryWrapper));
    }

    /**
     * 分页获取标签
     * @param tagQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Tag>> listTagPage(@RequestBody TagQueryRequest tagQueryRequest) {
        long current = tagQueryRequest.getCurrent();
        long size = tagQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Tag> tagPage = tagService.page(new Page<>(current, size), tagService.getQueryWrapper(tagQueryRequest));
        return ResultUtils.success(tagPage);
    }

    @PostMapping("/delete")
    public BaseResponse<String> deleteTag(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = tagService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success("删除成功");
    }


}
