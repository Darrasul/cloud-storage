package com.buzas.cloud.netty.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FirstInHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf message = (ByteBuf) msg;
        StringBuilder builder = new StringBuilder();
        log.info("received : {}", message);
        while (message.isReadable()){
            builder.append((char) message.readByte());
        }
        ctx.fireChannelRead(builder.toString());
    }
}
