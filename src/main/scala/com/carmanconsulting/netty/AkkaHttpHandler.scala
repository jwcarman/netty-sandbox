package com.carmanconsulting.netty

import akka.actor.{Props, ActorRef, ActorSystem}
import io.netty.channel.{ChannelHandlerAdapter, ChannelHandlerContext}
import io.netty.handler.codec.http.HttpRequest
import org.slf4j.LoggerFactory
import com.carmanconsulting.netty.actors.Dispatcher
import com.carmanconsulting.netty.messages.NettyHttpMessage

class AkkaHttpHandler(actorSystemName: String = "http-server") extends ChannelHandlerAdapter {
  val logger = LoggerFactory.getLogger(classOf[AkkaHttpHandler])
  val system: ActorSystem = ActorSystem(actorSystemName)
  val actorRef: ActorRef = system.actorOf(Props[Dispatcher])

  override def channelRead(ctx: ChannelHandlerContext, message: scala.Any): Unit = {
    message match {
      case req: HttpRequest =>
        logger.info("Received HttpRequest\n{}", req)
        actorRef ! new NettyHttpMessage(ctx, req)
      case _ => super.channelRead(ctx, message)
    }

  }
}
