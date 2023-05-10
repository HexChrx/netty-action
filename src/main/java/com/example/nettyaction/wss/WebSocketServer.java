package com.example.nettyaction.wss;

import com.example.nettyaction.common.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author ruixiang.crx
 * @date 2023/5/9
 */
public class WebSocketServer {
    public void run(String host, int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("http-codec", new HttpServerCodec())
                                    .addLast("http-aggregator", new HttpObjectAggregator(Constants.MAX_CONTENT_LENGTH))
                                    .addLast("http-chunked", new ChunkedWriteHandler())
                                    .addLast("fileServerHandler", new WebSocketServerHandler());
                        }
                    });
            Channel channel = b.bind(port).sync().channel();
            ChannelFuture future = b.bind(host, port).sync();
            System.out.printf("WebSocket server started at port: %d. Open your browser and navigate to http://%s:%d/",
                    port, host, port);

            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new WebSocketServer().run(Constants.HOST, Constants.PORT);
    }
}
