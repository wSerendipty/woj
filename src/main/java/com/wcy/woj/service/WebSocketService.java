package com.wcy.woj.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public interface WebSocketService {

    void connect(Channel channel);
    void removed(Channel channel);


    /**
     * 主动认证登录
     *
     * @param channel
     * @param token
     */
    void authorize(Channel channel, String token);


}
