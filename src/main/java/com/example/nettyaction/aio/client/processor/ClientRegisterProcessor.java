package com.example.nettyaction.aio.client.processor;

import com.alibaba.fastjson.JSON;
import com.example.nettyaction.aio.common.MessageTypeEnum;
import com.example.nettyaction.aio.common.model.Message;
import com.example.nettyaction.aio.common.model.RegisterResponse;
import com.example.nettyaction.aio.common.model.User;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class ClientRegisterProcessor implements ClientEventProcessor {
    @Override
    public String getType() {
        return MessageTypeEnum.REGISTER.name();
    }

    @Override
    public void process(Message message) {
        RegisterResponse response = JSON.parseObject(message.getContent(), RegisterResponse.class);
        User fromUser = message.getFromUser();

        if (fromUser == null) {
            fromUser = new User(0L, "未知用户", "");
        }

        System.out.printf("name:%s\tid:%s:\tID:%d%n", fromUser.getName(), fromUser.getId(), response.getUserId());
    }
}
