package com.tungee.com.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpRequest;

public class HttpProxyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest)msg;
            String host = request.headers().get("host");
            String uri = request.uri();
            // 链接至目标服务器
            Bootstrap bootstrap = new Bootstrap();
            // 注册线程池
            bootstrap.group(ctx.channel().eventLoop())
                    .channel(ctx.channel().getClass())
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new HttpProxyInitializer(ctx.channel()));
            ChannelFuture connect = bootstrap.connect("https://qyapi.weixin.qq.com", 443).sync();
            connect.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    future.channel().writeAndFlush(msg);
                } else {
                    ctx.channel().close();
                }
            }).channel().closeFuture().sync();
        }
        super.channelRead(ctx, msg);
    }
}
