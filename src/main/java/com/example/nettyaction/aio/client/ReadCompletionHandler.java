package com.example.nettyaction.aio.client;

import com.alibaba.fastjson.JSON;
import com.example.nettyaction.aio.client.processor.ClientEventProcessor;

import com.example.nettyaction.aio.common.model.Message;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * @author ruixiang.crx
 * @date 2023/4/28
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private final AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        String res = new String(body, StandardCharsets.UTF_8);
        System.out.println("The time server receive data: " + res);

        Message message = JSON.parseObject(res, Message.class);

        ClientEventProcessor processor = ProcessorManager.getProcessor(message.getType());
        if (processor == null) {
            System.out.println("未知事件类型： " + message.getType());
        } else {
            processor.process(message);
        }
        attachment.clear();
        channel.read(attachment, attachment, this);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        exc.printStackTrace();
    }

}
