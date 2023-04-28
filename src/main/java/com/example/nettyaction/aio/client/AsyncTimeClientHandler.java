package com.example.nettyaction.aio.client;

import com.alibaba.fastjson.JSON;
import com.example.nettyaction.aio.client.ReadCompletionHandler;
import com.example.nettyaction.aio.common.MessageTypeEnum;
import com.example.nettyaction.aio.common.model.Message;
import com.example.nettyaction.aio.common.prossor.LoginProcessor;
import com.example.nettyaction.aio.common.prossor.RegisterProcessor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * @author ruixiang.crx
 * @date 2023/4/28
 */
public class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {

    private AsynchronousSocketChannel client;
    private final String host;
    private final int port;
    private CountDownLatch latch;

    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        client.connect(new InetSocketAddress(host, port), this, this);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment) {
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        client.read(readBuffer, readBuffer, new ReadCompletionHandler(client));
        handlerEvent();
    }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    }

    private void handlerEvent() {
        Scanner scanner = new Scanner(System.in);
        String cmd;
        while (true) {
            System.out.println("请输入指令，s:发送消息，r:注册，q:退出登录，l:登录， kill:退出");
            cmd = scanner.nextLine();
            String[] cmdInfo = cmd.split(" ");
            try {
                Message message = new Message();
                switch (cmdInfo[0]) {
                    case "s":
                        message.setType(MessageTypeEnum.SEND_MESSAGE.name());
                        message.setToUser(Long.parseLong(cmdInfo[1]));
                        message.setContent(cmdInfo[2].getBytes(StandardCharsets.UTF_8));
                        doWrite(message);
                        break;
                    case "r":
                        message.setType(MessageTypeEnum.REGISTER.name());
                        message.setContent(JSON.toJSONBytes(RegisterProcessor.RegisterContent.builder()
                                .name(cmdInfo[1])
                                .password(cmdInfo[2])
                                .build()));
                        doWrite(message);
                        break;
                    case "l":
                        message.setType(MessageTypeEnum.LOGIN.name());
                        message.setContent(JSON.toJSONBytes(LoginProcessor.LoginContent.builder()
                                .id(Long.parseLong(cmdInfo[1]))
                                .password(cmdInfo[2])
                                .build()));
                        doWrite(message);
                        break;
                    case "exit":
                        client.close();
                        latch.countDown();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doWrite(Message message) {
        byte[] messageBytes = JSON.toJSONBytes(message);
        ByteBuffer writeBuffer = ByteBuffer.allocate(messageBytes.length);
        writeBuffer.put(messageBytes);
        writeBuffer.flip();
        client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (attachment.hasRemaining()) {
                    client.write(attachment, attachment, this);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
            }
        });
    }
}
