package com.example.nettyaction.aio.server;

import com.alibaba.fastjson.JSON;
import com.example.nettyaction.aio.common.ConnectionManager;
import com.example.nettyaction.aio.common.MessageTypeEnum;
import com.example.nettyaction.aio.common.ProcessorManager;
import com.example.nettyaction.aio.common.model.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * @author ruixiang.crx
 * @date 2023/4/28
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private final AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        if (body.length == 0) {
            ConnectionManager.closeConnection(channel);
            return;
        }
        attachment.get(body);
        String req = new String(body, StandardCharsets.UTF_8);
        System.out.println("The time server receive data: " + req);
        Message message = JSON.parseObject(req, Message.class);
        try {
            if (message != null) {
                boolean noUser = ConnectionManager.getConnection(channel) == null
                        || ConnectionManager.getConnection(channel).getUser() == null;
                if (noUser && !message.getType().equals(MessageTypeEnum.REGISTER.name())
                        && !message.getType().equals(MessageTypeEnum.LOGIN.name())) {
                    ConnectionManager.sendSystemMessage(channel, "请先登录或者注册".getBytes(StandardCharsets.UTF_8));
                } else {
                    message.setFromUser(ConnectionManager.getConnection(channel).getUser());
                    ProcessorManager.getProcessor(message.getType()).process(message, channel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            attachment.clear();
            if (channel.isOpen()) {
                channel.read(attachment, attachment, this);
            }
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        exc.printStackTrace();
    }
}
