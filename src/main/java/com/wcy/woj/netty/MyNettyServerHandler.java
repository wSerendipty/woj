package com.wcy.woj.netty;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.wcy.woj.common.BaseResponse;
import com.wcy.woj.common.ErrorCode;
import com.wcy.woj.common.ResultUtils;
import com.wcy.woj.service.WebSocketService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;



/**
 * @author 王长远
 * @version 1.0
 * @date 2023/9/9 20:53
 */
@Slf4j
public class MyNettyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    // 用来保存所有的客户端连接
    private WebSocketService webSocketService;

    private Gson gson = new Gson();

    private WebSocketService getService() {
        return SpringUtil.getBean(WebSocketService.class);
    }

    /**
     * 客户端连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.webSocketService = getService();
    }


    private void userOffLine(ChannelHandlerContext ctx) {
        this.webSocketService.removed(ctx.channel());
        ctx.channel().close();
    }

    /**
     * 用户离线
     * @param ctx
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        userOffLine(ctx);
        log.info("客户端断开连接！");
    }


    /**
     * 心跳检查
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                log.info("读空闲。。。。。。");
                userOffLine(ctx);
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            this.webSocketService.connect(ctx.channel());
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.COOKIE);
            if (StrUtil.isNotBlank(token)) {
                this.webSocketService.authorize(ctx.channel(), token);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("异常发生，异常消息 ={}", cause.getMessage());
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(ResultUtils.error(ErrorCode.SYSTEM_ERROR, "服务器异常"))));
        ctx.channel().close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        log.info("接收到的消息为：{}", msg.text());
    }

}
