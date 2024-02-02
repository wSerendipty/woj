package com.wcy.woj.service.impl;

import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.exception.BusinessException;
import com.wcy.woj.netty.NettyUtil;
import com.wcy.woj.service.WebSocketService;
import com.wcy.woj.utils.RedisUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/30 9:27
 */
@Service
public class WebsocketServiceImpl implements WebSocketService {
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void connect(Channel channel) {

    }

    @Override
    public void removed(Channel channel) {

    }

    @Override
    public void authorize(Channel channel, String cookie) {
        String loginCookie = NettyUtil.getAttr(channel, NettyUtil.COOKIE);
        Object o = redisUtil.get(loginCookie);
        if (o == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        System.out.println(o);
    }
}
