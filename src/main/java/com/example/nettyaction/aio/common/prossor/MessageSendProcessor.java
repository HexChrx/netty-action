package com.example.nettyaction.aio.common.prossor;


import com.example.nettyaction.aio.common.UserManager;
import com.example.nettyaction.aio.common.ConnectionManager;
import com.example.nettyaction.aio.common.MessageTypeEnum;
import com.example.nettyaction.aio.common.model.Message;
import com.example.nettyaction.aio.common.model.User;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class MessageSendProcessor implements MessageProcessor {
    @Override
    public String getType() {
        return MessageTypeEnum.SEND_MESSAGE.name();
    }

    @Override
    public void process(Message message, AsynchronousSocketChannel socketChannel) throws IOException {
        User toUser = UserManager.getUserInfo(message.getToUser());
        if (toUser == null) {
            ConnectionManager.sendSystemMessage(socketChannel, "目标用户不存在".getBytes());
            return;
        }

        if (!ConnectionManager.isUserOnline(toUser.getId())) {
            ConnectionManager.sendSystemMessage(socketChannel, "目标用户不在线".getBytes());
            return;
        }

        ConnectionManager.send(message.getFromUser(), toUser.getId(), message.getContent());
    }

}
