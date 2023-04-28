package com.example.nettyaction.aio.client.processor;


import com.example.nettyaction.aio.common.model.Message;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public interface ClientEventProcessor {
    String getType();

    void process(Message message);
}
