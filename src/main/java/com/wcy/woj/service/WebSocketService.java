package com.wcy.woj.service;

import io.netty.channel.Channel;

public interface WebSocketService {

    void connect(Channel channel);
    void removed(Channel channel);


    /**
     * 主动认证登录
     *
     * @param channel
     * @param cookie
     */
    void authorize(Channel channel, String cookie);


}
