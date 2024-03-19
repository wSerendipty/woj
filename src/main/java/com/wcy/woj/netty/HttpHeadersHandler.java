package com.wcy.woj.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;


/**
 * @author 王长远
 * @version 1.0
 * @date 2023/9/10 13:25
 */
@Slf4j
public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            // 获取websocket 携带的cookie
            String cookie = request.headers().get("Cookie");
            System.out.println(cookie.split("=")[1]);
            // 记录cookie即可实现登录认证
            NettyUtil.setAttr(ctx.channel(), NettyUtil.COOKIE, cookie.split("=")[1]);
            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
