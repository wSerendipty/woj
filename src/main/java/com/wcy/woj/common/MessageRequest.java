package com.wcy.woj.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/3/19 10:26
 */
@Data
public class MessageRequest implements Serializable {
    private Integer type;
    private static final long serialVersionUID = 1L;
}
