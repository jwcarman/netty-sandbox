package com.carmanconsulting.netty

import io.netty.channel.nio.NioEventLoopGroup
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel._
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{HttpContentCompressor, HttpResponseEncoder, HttpRequestDecoder, HttpObjectAggregator}
import io.netty.util.internal.logging.{Slf4JLoggerFactory, InternalLoggerFactory}
import io.netty.util.ResourceLeakDetector
import io.netty.handler.stream.ChunkedWriteHandler
import akka.actor.{Props, ActorRef, ActorSystem}
import com.carmanconsulting.netty.actors.Dispatcher

object AkkaHttpServer {
  def main(args: Array[String]) {
    InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory())
    ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED)
    val parentGroup: NioEventLoopGroup = new NioEventLoopGroup(1)
    val childGroup: NioEventLoopGroup = new NioEventLoopGroup()
    val system: ActorSystem = ActorSystem("http-server")
    val dispatcher: ActorRef = system.actorOf(Props[Dispatcher], "dispatcher")
    try {
      val bootstrap: ServerBootstrap = new ServerBootstrap()
      bootstrap.group(parentGroup, childGroup)
      bootstrap.channel(classOf[NioServerSocketChannel])
      bootstrap.childHandler(new ChannelInitializer[SocketChannel]() {
        override def initChannel(ch: SocketChannel): Unit = {
          val pipeline: ChannelPipeline = ch.pipeline
          pipeline.addLast("decoder", new HttpRequestDecoder())
          pipeline.addLast("aggregator", new HttpObjectAggregator(1048576))
          pipeline.addLast("encoder", new HttpResponseEncoder())
          pipeline.addLast("chunkedWriter", new ChunkedWriteHandler())
          pipeline.addLast("deflater", new HttpContentCompressor())
          pipeline.addLast("handler", new AkkaHttpHandler(dispatcher))
        }
      })
      val ch: Channel = bootstrap.bind(8080).sync.channel
      ch.closeFuture.sync
    }
    finally {
      parentGroup.shutdownGracefully()
      childGroup.shutdownGracefully()
      parentGroup.terminationFuture().sync()
      childGroup.terminationFuture().sync()
    }
  }
}
