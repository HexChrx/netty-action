package com.example.nettyaction.aio.common.model;

import lombok.Data;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
@Data
public class Connection {
    private User user;

    private AsynchronousSocketChannel socketChannel;

    private Long lastTouchTime;

    public Connection(User user, AsynchronousSocketChannel socketChannel) {
        this.user = user;
        this.socketChannel = socketChannel;
        this.lastTouchTime = System.currentTimeMillis();
    }
}
