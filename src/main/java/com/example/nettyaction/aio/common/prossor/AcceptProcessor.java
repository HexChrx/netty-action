package com.example.nettyaction.aio.common.prossor;


import com.example.nettyaction.aio.common.ConnectionManager;
import com.example.nettyaction.aio.common.MessageTypeEnum;
import com.example.nettyaction.aio.common.model.Message;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class AcceptProcessor implements MessageProcessor {
    @Override
    public String getType() {
        return MessageTypeEnum.ACCEPT.name();
    }

    @Override
    public void process(Message message, AsynchronousSocketChannel socketChannel) throws IOException {
        ConnectionManager.createConnection(socketChannel);
        ConnectionManager.sendSystemMessage(socketChannel, "连接成功".getBytes(StandardCharsets.UTF_8));
    }
}
