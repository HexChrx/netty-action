package com.example.nettyaction.aio.common.prossor;


import com.example.nettyaction.aio.common.model.Message;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public interface MessageProcessor {
    String getType();
    void process(Message message, AsynchronousSocketChannel socketChannel) throws IOException;
}
