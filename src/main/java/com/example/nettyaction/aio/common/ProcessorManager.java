package com.example.nettyaction.aio.common;

import com.example.nettyaction.aio.common.prossor.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class ProcessorManager {
    private static final Map<String, MessageProcessor> PROCESSOR_MAP;

    static {
        PROCESSOR_MAP = new HashMap<>(4);
        AcceptProcessor acceptProcessor = new AcceptProcessor();
        PROCESSOR_MAP.put(acceptProcessor.getType(), acceptProcessor);

        LoginProcessor loginProcessor = new LoginProcessor();
        PROCESSOR_MAP.put(loginProcessor.getType(), loginProcessor);

        MessageSendProcessor messageSendProcessor = new MessageSendProcessor();
        PROCESSOR_MAP.put(messageSendProcessor.getType(), messageSendProcessor);

        RegisterProcessor registerProcessor = new RegisterProcessor();
        PROCESSOR_MAP.put(registerProcessor.getType(), registerProcessor);

    }

    public static MessageProcessor getProcessor(String type) {
        return PROCESSOR_MAP.get(type);
    }
}
