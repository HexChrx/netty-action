package com.example.nettyaction.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @author ruixiang.crx
 * @date 2023/5/9
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");


    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        if (fullHttpRequest.decoderResult().isFailure()) {

        }

        if (fullHttpRequest.method() != HttpMethod.GET) {

        }

        final String uri = fullHttpRequest.uri();
        final String path ;

    }

    private String sanitizeUri(String uri) {
        try {
            uri = URLDecoder.decode(uri, StandardCharsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, StandardCharsets.ISO_8859_1.displayName());
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }

        uri = uri.replace('/', File.separatorChar);
        return uri;
    }
}
