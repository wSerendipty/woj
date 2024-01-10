package com.wcy.woj.model.dto.tag;

import lombok.Data;
import java.io.Serializable;


@Data
public class TagQueryRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签所属类型（question/post）
     */
    private String belongType;

    private static final long serialVersionUID = 1L;
}