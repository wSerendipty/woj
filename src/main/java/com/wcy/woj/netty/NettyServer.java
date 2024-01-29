package com.wcy.woj.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author 王长远
 * @version 1.0
 * @date 2023/9/9 20:53
 */
@Slf4j
@Configuration
public class NettyServer {

    private NioEventLoopGroup bossGroup = new NioEventLoopGroup(3);
    private NioEventLoopGroup workGroup = new NioEventLoopGroup();

    @Value("${ws.port}")
    private int wsPort;


    /**
     * 启动 ws server
     *
     * @return
     */
    @PostConstruct
    public void start()  {
        run();
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        Future<?> future = bossGroup.shutdownGracefully();
        Future<?> future1 = workGroup.shutdownGracefully();
        future.syncUninterruptibly();
        future1.syncUninterruptibly();
        log.info("关闭 ws server 成功");
    }

    public void run() {
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workGroup)
                    //使用哪种通道实现,NioServerSocketChannel为异步的服务器端 TCP Socket 连接。
                    .channel(NioServerSocketChannel.class)
                    //设置连接队列长度
                    .option(ChannelOption.SO_BACKLOG,1024)
                    //添加拦截处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //30秒没有发生心跳则关闭连接
                            pipeline.addLast(new IdleStateHandler(30,0,0));
                            //websocket协议本身是基于http协议的，所以使用http解编码器
                            pipeline.addLast(new HttpServerCodec());
                            // 以块的方式写
                            pipeline.addLast(new ChunkedWriteHandler());
                            //post请求方式
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            // 处理http请求
                            pipeline.addLast(new HttpHeadersHandler());
                            /**
                             * 说明：
                             *  1. 对于 WebSocket，它的数据是以帧frame 的形式传递的；
                             *  2. 可以看到 WebSocketFrame 下面有6个子类
                             *  3. 浏览器发送请求时： ws://localhost:7000/hello 表示请求的uri
                             *  4. WebSocketServerProtocolHandler 核心功能是把 http协议升级为 ws 协议，保持长连接；
                             *      是通过一个状态码 101 来切换的
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
                            // 自定义handle 处理业务逻辑
                            pipeline.addLast(new MyNettyServerHandler());
                        }
                    });
            bootstrap.bind(wsPort).sync();
            log.info("netty 服务开启成功！=====>{}",wsPort);
        }catch (Exception e){
            log.info("netty启动错误！{}",e.getMessage());
        }
    }
}