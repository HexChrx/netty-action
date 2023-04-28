package com.example.nettyaction.aio.common.prossor;

import com.alibaba.fastjson.JSON;
import com.example.nettyaction.aio.common.UserManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.nettyaction.aio.common.ConnectionManager;
import com.example.nettyaction.aio.common.MessageTypeEnum;
import com.example.nettyaction.aio.common.model.Message;
import com.example.nettyaction.aio.common.model.User;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class LoginProcessor implements MessageProcessor {
    @Override
    public String getType() {
        return MessageTypeEnum.LOGIN.name();
    }

    @Override
    public void process(Message message, AsynchronousSocketChannel socketChannel) throws IOException {
        LoginContent content = JSON.parseObject(message.getContent(), LoginContent.class);
        if (content == null) {
            ConnectionManager.sendSystemMessage(socketChannel, "id和密码不能为空".getBytes(StandardCharsets.UTF_8));
            return;
        }

        User user = UserManager.getUserInfo(content.getId());
        if (user == null) {
            ConnectionManager.sendSystemMessage(socketChannel, "用户不存在，请确认ID是否正确".getBytes(StandardCharsets.UTF_8));
            return;
        }

        if (!user.getPassword().equals(content.getPassword())) {
            ConnectionManager.sendSystemMessage(socketChannel, "密码错误".getBytes(StandardCharsets.UTF_8));
            return;
        }

        ConnectionManager.bindConnection(user, socketChannel);
        ConnectionManager.sendSystemMessage(socketChannel, "登录成功".getBytes(StandardCharsets.UTF_8));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginContent {
        private Long id;
        private String password;
    }
}
