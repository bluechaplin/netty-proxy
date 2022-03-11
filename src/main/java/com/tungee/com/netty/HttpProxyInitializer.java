package com.tungee.com.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class HttpProxyInitializer extends ChannelInitializer {

    private Channel clientChannel;

    public HttpProxyInitializer(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(6553600));
        ch.pipeline().addLast(new HttpContentDecompressor());
        ch.pipeline().addLast(new HttpProxyClientHandle(clientChannel));
    }
}
