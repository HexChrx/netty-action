package com.example.nettyaction.aio.server;

/**
 * @author ruixiang.crx
 * @date 2023/4/28
 */
public class AioTimeServer {
    public static void main(String[] args) {
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(9999);
        new Thread(timeServer, "AIO-AsyncTimeServerHandler-001").start();
    }
}
