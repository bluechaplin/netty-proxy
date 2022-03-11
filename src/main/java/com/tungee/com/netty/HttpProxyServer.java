package com.tungee.com.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpProxyServer {
    int port;
    public HttpProxyServer(int port) {
        this.port = port;
    }
    public static void main(String[] args) throws Exception {
        new HttpProxyServer(8443).run();
    }
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                                    .addLast(new HttpResponseEncoder())
                                    // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                                    .addLast(new HttpRequestDecoder())
                                    // 增加自定义实现的Handler
                                    .addLast(new LoggingHandler(LogLevel.DEBUG), new HttpProxyServerHandler());
                        }
                    })
                    .bind(port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}