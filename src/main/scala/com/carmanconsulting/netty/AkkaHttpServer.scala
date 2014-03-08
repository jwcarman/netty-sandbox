package com.carmanconsulting.netty

import io.netty.channel.nio.NioEventLoopGroup
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel._
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http._
import io.netty.util.internal.logging.{Slf4JLoggerFactory, InternalLoggerFactory}
import akka.actor.{Props, ActorSystem}
import com.carmanconsulting.netty.actors.Dispatcher

object AkkaHttpServer {
  def main(args: Array[String]) {
    InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory())
    val parentGroup = new NioEventLoopGroup()
    val childGroup = new NioEventLoopGroup()
    val system = ActorSystem("http-server")
    val recipient = system.actorOf(Props[Dispatcher])

    try {
      val bootstrap: ServerBootstrap = new ServerBootstrap()
      bootstrap.group(parentGroup, childGroup)
      bootstrap.channel(classOf[NioServerSocketChannel])
      bootstrap.childHandler(new ChannelInitializer[SocketChannel]() {
        override def initChannel(ch: SocketChannel): Unit = {
          val pipeline: ChannelPipeline = ch.pipeline
          pipeline.addLast("codec", new HttpServerCodec())
          pipeline.addLast("handler", AkkaHttpHandler(recipient))
        }
      })
      val channel: Channel = bootstrap.bind(8888).sync.channel
      channel.closeFuture.sync
    }
    finally {
      parentGroup.shutdownGracefully()
      childGroup.shutdownGracefully()
      parentGroup.terminationFuture().sync()
      childGroup.terminationFuture().sync()
    }
  }
}
