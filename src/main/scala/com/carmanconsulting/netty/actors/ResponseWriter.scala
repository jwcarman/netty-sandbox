package com.carmanconsulting.netty.actors

import akka.actor.Actor
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext}
import akka.event.LoggingReceive
import io.netty.handler.codec.http.HttpResponse
import org.slf4j.LoggerFactory

class ResponseWriter(ctx: ChannelHandlerContext) extends Actor {
  val logger = LoggerFactory.getLogger(classOf[ResponseWriter])

  override def receive: Actor.Receive = LoggingReceive {
    case resp: HttpResponse =>
      logger.info("Response received, writing to channel...\n{}", resp)
      ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE)
      context.stop(self)
  }
}
