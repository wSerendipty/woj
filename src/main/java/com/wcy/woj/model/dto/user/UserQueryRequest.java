package com.wcy.woj.model.dto.user;

import com.wcy.woj.common.PageRequest;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 *

 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;


    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 开始时间
     */
    private Date StartTime;

    /**
     * 结束时间
     */
    private Date EndTime;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}