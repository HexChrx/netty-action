package com.example.nettyaction.aio.common;


import com.alibaba.fastjson.JSON;
import com.example.nettyaction.aio.common.model.Connection;
import com.example.nettyaction.aio.common.model.Message;
import com.example.nettyaction.aio.common.model.User;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class ConnectionManager {
    private static final Map<Long, Connection> CONNECTION_MAP = new ConcurrentHashMap<>();

    private static final Map<Channel, Connection> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static void createConnection(AsynchronousSocketChannel channel) {
        Connection connection = new Connection(null, channel);
        CHANNEL_MAP.put(channel, connection);
    }

    public static void bindConnection(User user, AsynchronousSocketChannel channel) {
        Connection connection = CHANNEL_MAP.get(channel);
        if (connection != null) {
            CONNECTION_MAP.put(user.getId(), connection);
            connection.setUser(user);
        }
    }

    public static Connection getConnection(Long userId) {
        return CONNECTION_MAP.get(userId);
    }

    public static Connection getConnection(AsynchronousSocketChannel channel) {
        return CHANNEL_MAP.get(channel);
    }

    public static void closeConnection(AsynchronousSocketChannel channel) {
        Connection connection = CHANNEL_MAP.get(channel);
        if (connection != null) {
            CHANNEL_MAP.remove(channel);
            CONNECTION_MAP.remove(connection.getUser().getId());
            System.out.printf("user: %d %s 断开连接%n", connection.getUser().getId(), connection.getUser().getName());
        }
    }

    public static boolean isUserOnline(Long userId) {
        return getConnection(userId) != null;
    }

    public static void send(AsynchronousSocketChannel socketChannel, Message message) throws IOException {

        byte[] messageBytes = JSON.toJSONBytes(message);
        ByteBuffer writeBuffer = ByteBuffer.allocate(messageBytes.length);
        writeBuffer.put(messageBytes);
        writeBuffer.flip();
        socketChannel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (attachment.hasRemaining()) {
                    socketChannel.write(attachment, attachment, this);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
            }
        });
        System.out.println("发送消息：" + JSON.toJSON(message));
    }

    public static void send(User fromUser, Long toUser, byte[] content) throws IOException {
        Connection connection = CONNECTION_MAP.get(toUser);
        if (connection != null) {
            Message message = Message.builder()
                    .type(MessageTypeEnum.SEND_MESSAGE.name())
                    .content(content)
                    .fromUser(fromUser)
                    .toUser(toUser)
                    .build();

            send(connection.getSocketChannel(), message);
        }
    }

    public static void send(User fromUser, AsynchronousSocketChannel socketChannel, byte[] content) throws IOException {

        if (socketChannel != null) {
            Message message = Message.builder()
                    .type(MessageTypeEnum.SEND_MESSAGE.name())
                    .content(content)
                    .fromUser(fromUser)
                    .toUser(null)
                    .build();

            send(socketChannel, message);
        }
    }

    public static void sendSystemMessage(Long toUser, byte[] content) throws IOException {
        send(UserManager.getSystemUser(), toUser, content);
    }


    public static void sendSystemMessage(AsynchronousSocketChannel channel, byte[] content) throws IOException {
        send(UserManager.getSystemUser(), channel, content);
    }

}
