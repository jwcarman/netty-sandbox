package com.carmanconsulting.netty;

import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.CharsetUtil;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyHttpServer {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServer.class);

//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    private static String bestMatch(String path, PatriciaTrie<String> trie) {
        String value = trie.get(path);
        if(value != null) {
            return value;
        }
        else {
            NavigableMap<String,String> head = new TreeMap<String, String>(trie.headMap(path));
            for (Map.Entry<String,String> entry : head.descendingMap().entrySet()) {
                if(path.startsWith(entry.getKey())) {
                    return entry.getValue();
                }
            }
            return null;
        }
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    private static class HttpHandler extends ChannelHandlerAdapter {
        private final PatriciaTrie<String> replies;
        private final ActorSystem actorSystem;

        private HttpHandler() {
            replies = new PatriciaTrie<String>();
            replies.put("/hello", "Hello, World!");
            replies.put("/", "The default page!");
            actorSystem = ActorSystem.create("http-handler");
        }

//        @Override
//        public void channelReadComplete(ChannelHandlerContext context) {
//            context.flush();
//        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            LOGGER.info("channelRead()\n{}", msg);
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;
                String reply = replies.get(req.getUri());
                if (reply == null) {
                    ctx.write(new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
                } else {
                    if (is100ContinueExpected(req)) {
                        ctx.writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
                    } else {
                        boolean keepAlive = isKeepAlive(req);
                        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(reply, CharsetUtil.UTF_8));
                        response.headers().set(CONTENT_TYPE, "text/plain");
                        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

                        if (!keepAlive) {
                            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                        } else {
                            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                            ctx.writeAndFlush(response);
                        }
                    }
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOGGER.error("An exception was caught: {}", cause);
            ctx.close();
        }
    }

    private static class HttpInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();


            pipeline.addLast("codec", new HttpServerCodec());
            pipeline.addLast("handler", new HttpHandler());
        }
    }

    private static class HttpRequestActor extends UntypedActor


    {
        @Override
        public void onReceive(Object message) throws Exception {

        }
    }

//----------------------------------------------------------------------------------------------------------------------
// main() method
//----------------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        PatriciaTrie<String> trie = new PatriciaTrie<String>();
        trie.put("/foo/loo", "hello");
        trie.put("/foo/bar", "oops");
        trie.put("/foo", "world");

        System.out.println(bestMatch("/foo", trie));
        System.out.println(bestMatch("/foo/baz", trie));
        System.out.println(bestMatch("/foo/bar/baz", trie));

//        NioEventLoopGroup parentGroup = new NioEventLoopGroup(1);
//        NioEventLoopGroup childGroup = new NioEventLoopGroup();
//        try {
//            ServerBootstrap bootstrap = new ServerBootstrap();
//            bootstrap.group(parentGroup, childGroup);
//            bootstrap.channel(NioServerSocketChannel.class);
//            bootstrap.childHandler(new HttpInitializer());
//            Channel ch = bootstrap.bind(8080).sync().channel();
//            ch.closeFuture().sync();
//        }
//        finally {
//            parentGroup.shutdownGracefully();
//            childGroup.shutdownGracefully();
//        }
    }
}
