package com.carmanconsulting.netty.actors

import akka.actor.{ReceiveTimeout, Actor}
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext}
import akka.event.LoggingReceive
import io.netty.handler.codec.http.{DefaultFullHttpResponse, HttpResponse}
import scala.concurrent.duration._
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http.HttpResponseStatus._
import com.typesafe.scalalogging.slf4j.Logging

class ResponseWriter(ctx: ChannelHandlerContext) extends Actor with Logging {
  context.setReceiveTimeout(5 seconds)

  override def receive: Actor.Receive = LoggingReceive {
    case resp: HttpResponse =>
      logger.info("Response received, writing to channel...")
      respondAndStop(resp)
    case ReceiveTimeout =>
      logger.warn("Timed out waiting for response.")
      respondAndStop(new DefaultFullHttpResponse(HTTP_1_1, REQUEST_TIMEOUT))
  }

  def respondAndStop(response: HttpResponse) {
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    context.stop(self)
  }
}
