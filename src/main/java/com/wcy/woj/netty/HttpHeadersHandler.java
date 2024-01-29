package com.wcy.woj.netty;

import cn.hutool.core.net.url.UrlBuilder;
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
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
            // 获取请求路径
            request.setUri(urlBuilder.getPath().toString());
            // 获取cookie
            String cookie = request.headers().get("Cookie");
            NettyUtil.setAttr(ctx.channel(), NettyUtil.COOKIE, cookie);

            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);
        }
        else {
            ctx.fireChannelRead(msg);
        }
    }
}
