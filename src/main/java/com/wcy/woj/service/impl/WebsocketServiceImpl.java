package com.wcy.woj.service.impl;

import com.google.gson.Gson;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.exception.ThrowUtils;
import com.wcy.woj.model.entity.User;
import com.wcy.woj.netty.NettyUtil;
import com.wcy.woj.service.WebSocketService;
import com.wcy.woj.utils.RedisUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 王长远
 * @version 1.0
 * @date 2024/1/30 9:27
 */
@Service
@Slf4j
public class WebsocketServiceImpl implements WebSocketService {
    @Resource
    private RedisUtil redisUtil;

    private static final Gson gson = new Gson();

    /**
     * 所有在线的用户和对应的socket
     */
    private static final ConcurrentHashMap<Long, Channel> ONLINE_UID_MAP = new ConcurrentHashMap<>();


    /**
     * 所有已连接的websocket连接列表和一些额外参数
     */
    private static final ConcurrentHashMap<Channel, Long> ONLINE_WS_MAP = new ConcurrentHashMap<>();


    @Override
    public void connect(Channel channel) {
        String cookie = NettyUtil.getAttr(channel, NettyUtil.COOKIE);
        Object userObj = redisUtil.get(cookie);
        ThrowUtils.throwIf(userObj == null, ErrorCode.NOT_LOGIN_ERROR, ErrorCode.NOT_LOGIN_ERROR.getMessage());
        User user = gson.fromJson(userObj.toString(),User.class);
        ONLINE_WS_MAP.put(channel, user.getId());
        log.info("连接成功！");
    }

    @Override
    public void removed(Channel channel) {
        Long aLong = ONLINE_WS_MAP.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(aLong);
        boolean offlineAll = offline(channel, uidOptional);
        if (uidOptional.isPresent() && offlineAll) {//已登录用户断连,并且全下线成功
            log.info("退出成功！");
        }

    }

    /**
     * 用户下线
     * return 是否全下线成功
     */
    private boolean offline(Channel channel, Optional<Long> uidOptional) {
        ONLINE_WS_MAP.remove(channel);
        if (uidOptional.isPresent()) {
            ONLINE_UID_MAP.remove(uidOptional.get());
            return ObjectUtils.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
    }

    @Override
    public void authorize(Channel channel, String cookie) {
        Object userObj = redisUtil.get(cookie);
        ThrowUtils.throwIf(userObj == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");
        System.out.println("认证！");
    }
}
