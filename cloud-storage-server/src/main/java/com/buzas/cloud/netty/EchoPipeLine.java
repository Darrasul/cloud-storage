package com.buzas.cloud.netty;

import com.buzas.cloud.netty.handlers.SecondInHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class EchoPipeLine extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(
            new StringEncoder(),
                new StringDecoder(),
                new SecondInHandler()
        );
    }
}
