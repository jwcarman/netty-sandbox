package com.carmanconsulting.netty.actors

import akka.actor.{ReceiveTimeout, Actor}
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext}
import akka.event.LoggingReceive
import io.netty.handler.codec.http.{DefaultFullHttpResponse, DefaultFullHttpRequest, HttpResponse}
import org.slf4j.LoggerFactory
import scala.concurrent.duration._
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http.HttpResponseStatus._

class ResponseWriter(ctx: ChannelHandlerContext) extends Actor {
  val logger = LoggerFactory.getLogger(classOf[ResponseWriter])
  context.setReceiveTimeout(30 seconds)

  override def receive: Actor.Receive = LoggingReceive {
    case resp: HttpResponse =>
      logger.info("Response received, writing to channel...\n{}", resp)
      ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE)
      context.stop(self)
    case ReceiveTimeout =>
      logger.warn("Timed out waiting for response.")
      val response = new DefaultFullHttpResponse(HTTP_1_1, REQUEST_TIMEOUT)
      ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
      context.stop(self)
  }
}
