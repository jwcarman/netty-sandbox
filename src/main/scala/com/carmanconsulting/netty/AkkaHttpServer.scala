package com.carmanconsulting.netty

import io.netty.channel.nio.NioEventLoopGroup
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel._
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpServerCodec
import java.util.concurrent.{Executors, ExecutorService}

object AkkaHttpServer {
  def main(args: Array[String]) {
    val executor:ExecutorService = Executors.newFixedThreadPool(10)

    val parentGroup: NioEventLoopGroup = new NioEventLoopGroup(100)
    val childGroup: NioEventLoopGroup = new NioEventLoopGroup()
    val codec: HttpServerCodec = new HttpServerCodec
    val handler: AkkaHttpHandler = new AkkaHttpHandler
    try {
      val bootstrap: ServerBootstrap = new ServerBootstrap
      bootstrap.group(parentGroup, childGroup)
      bootstrap.channel(classOf[NioServerSocketChannel])

      bootstrap.childHandler(new ChannelInitializer[SocketChannel]() {
        override def initChannel(ch: SocketChannel): Unit = {
          val pipeline: ChannelPipeline = ch.pipeline
          pipeline.addLast("codec", codec)
          pipeline.addLast("handler", handler)
        }
      })
      val ch: Channel = bootstrap.bind(8080).sync.channel
      ch.closeFuture.sync
    }
    finally {
      parentGroup.shutdownGracefully()
      childGroup.shutdownGracefully()
    }
  }
}
