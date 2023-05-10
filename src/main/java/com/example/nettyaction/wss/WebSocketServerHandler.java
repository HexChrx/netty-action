package com.example.nettyaction.wss;

import com.example.nettyaction.common.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpHeaderUtil.setContentLength;
import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;

/**
 * @author ruixiang.crx
 * @date 2023/5/9
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = Logger
            .getLogger(WebSocketServerHandler.class.getName());

    private static final String WS_URI = String.format("ws://%s:%d/websocket", Constants.HOST, Constants.PORT);

    private WebSocketServerHandshaker handshake;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (req.decoderResult().isFailure() || !HttpHeaderValues.WEBSOCKET.contentEquals(req.headers().get(HttpHeaderNames.UPGRADE))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory wsFactor = new WebSocketServerHandshakerFactory(WS_URI, null, false);
        handshake = wsFactor.newHandshaker(req);
        if (handshake == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshake.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshake.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
            return;
        }

        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        String request = ((TextWebSocketFrame) frame).text();
        logger.fine(String.format("%s received %s", ctx.channel(), request));
        ctx.channel().write(new TextWebSocketFrame(request + ", 欢迎使用Netty WebSocket服务，现在时刻是："
                + new Date()));
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse resp) {
        if (resp.status().code() != HttpResponseStatus.OK.code()) {
            ByteBuf buf = Unpooled.copiedBuffer(resp.status().toString(), StandardCharsets.UTF_8);
            resp.content().writeBytes(buf);
            buf.release();
            setContentLength(resp, resp.content().readableBytes());
        }

        ChannelFuture f = ctx.channel().writeAndFlush(resp);
        if (!isKeepAlive(req) || resp.status().code() != HttpResponseStatus.OK.code()) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
