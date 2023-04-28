package com.example.nettyaction.aio.client;

/**
 * @author ruixiang.crx
 * @date 2023/4/28
 */
public class AioTimeClient {
    public static void main(String[] args) {
        new Thread(new AsyncTimeClientHandler("127.0.0.1", 9999)).start();
    }
}
