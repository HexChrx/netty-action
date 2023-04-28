package com.example.nettyaction.aio.client.processor;


import com.example.nettyaction.aio.common.MessageTypeEnum;
import com.example.nettyaction.aio.common.model.Message;
import com.example.nettyaction.aio.common.model.User;

import java.nio.charset.StandardCharsets;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class ClientMessageSendProcessor implements ClientEventProcessor {
    @Override
    public String getType() {
        return MessageTypeEnum.SEND_MESSAGE.name();
    }

    @Override
    public void process(Message message) {
        User fromUser = message.getFromUser();

        if (fromUser == null) {
            fromUser = new User(0L, "未知用户", "");
        }
        System.out.printf("name: %s\tid:%s:\t%s%n", fromUser.getName(), fromUser.getId(), new String(message.getContent(), StandardCharsets.UTF_8));
    }
}
