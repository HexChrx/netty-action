package com.example.nettyaction.netty;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.Date;


/**
 * @author ruixiang.crx
 * @date 2023/4/28
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    private int counter;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String body = (String) msg;
        System.out.printf("The time server receive order : %s; the counter is : %d%n", body, ++counter);
        String currentTime = ("QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER")
                + System.lineSeparator();
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
