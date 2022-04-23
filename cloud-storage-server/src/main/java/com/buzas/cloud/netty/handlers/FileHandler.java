package com.buzas.cloud.netty.handlers;

import com.buzas.cloud.model.AbstractMessage;
import com.buzas.cloud.model.FileMessage;
import com.buzas.cloud.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class FileHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private final Path serverDirectory = Path.of("cloudFiles");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ListMessage message = new ListMessage(serverDirectory);
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ListMessage message = new ListMessage(serverDirectory);
        ctx.writeAndFlush(message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) throws Exception {
        log.info("received : {} message", message.getMessageType().getName());
        if (message instanceof FileMessage fileMessage){
            Files.write(serverDirectory.resolve(fileMessage.getName()), fileMessage.getBytes());
        }
    }
}
