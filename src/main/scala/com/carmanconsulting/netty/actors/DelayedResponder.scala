package com.carmanconsulting.netty.actors

import scala.concurrent.duration._
import akka.actor.Actor
import akka.event.LoggingReceive
import io.netty.handler.codec.http.{DefaultFullHttpResponse, HttpRequest}
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import io.netty.handler.codec.http.HttpHeaders.Names._
import com.carmanconsulting.netty.messages.DelayedResponse

class DelayedResponder extends Actor {
  override def receive: Actor.Receive = LoggingReceive {
    case req: HttpRequest =>
      implicit val dispatcher = context.system.dispatcher
      context.system.scheduler.scheduleOnce(45 seconds, self, DelayedResponse(sender))
    case DelayedResponse(receiver) =>
      val response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer("Timeout!", CharsetUtil.UTF_8))
      response.headers.set(CONTENT_TYPE, "text/plain")
      response.headers.set(CONTENT_LENGTH, response.content.readableBytes)
      receiver ! response

  }
}
