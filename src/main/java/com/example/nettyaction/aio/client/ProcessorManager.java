package com.example.nettyaction.aio.client;


import com.example.nettyaction.aio.client.processor.ClientEventProcessor;
import com.example.nettyaction.aio.client.processor.ClientMessageSendProcessor;
import com.example.nettyaction.aio.client.processor.ClientRegisterProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class ProcessorManager {
    private static final Map<String, ClientEventProcessor> PROCESSOR_MAP;

    static {
        PROCESSOR_MAP = new HashMap<>(4);
        ClientMessageSendProcessor messageSendProcessor = new ClientMessageSendProcessor();
        PROCESSOR_MAP.put(messageSendProcessor.getType(), messageSendProcessor);

        ClientRegisterProcessor clientRegisterProcessor = new ClientRegisterProcessor();
        PROCESSOR_MAP.put(clientRegisterProcessor.getType(), clientRegisterProcessor);

    }

    public static ClientEventProcessor getProcessor(String type) {
        return PROCESSOR_MAP.get(type);
    }
}
