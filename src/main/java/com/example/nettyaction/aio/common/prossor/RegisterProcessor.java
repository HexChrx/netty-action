package com.example.nettyaction.aio.common.prossor;

import com.alibaba.fastjson.JSON;
import com.example.nettyaction.aio.common.ConnectionManager;
import com.example.nettyaction.aio.common.UserManager;
import com.example.nettyaction.aio.common.model.Message;
import com.example.nettyaction.aio.common.model.RegisterResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.nettyaction.aio.common.MessageTypeEnum;
import com.example.nettyaction.aio.common.model.User;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class RegisterProcessor implements MessageProcessor {

    @Override
    public String getType() {
        return MessageTypeEnum.REGISTER.name();
    }

    @Override
    public void process(Message message, AsynchronousSocketChannel socketChannel) throws IOException {
        RegisterContent content = JSON.parseObject(message.getContent(), RegisterContent.class);
        if (content == null) {
            ConnectionManager.sendSystemMessage(socketChannel, "用户名和密码不能为空".getBytes(StandardCharsets.UTF_8));
            return;
        }
        User user = UserManager.createUser(content.name, content.password);
        ConnectionManager.bindConnection(user, socketChannel);

        Message responseMessage = new Message();
        responseMessage.setType(message.getType());
        responseMessage.setFromUser(UserManager.getSystemUser());
        responseMessage.setContent(JSON.toJSONBytes(new RegisterResponse(user.getId())));
        responseMessage.setToUser(user.getId());
        ConnectionManager.send(socketChannel, responseMessage);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterContent {
        private String name;
        private String password;
    }
}
