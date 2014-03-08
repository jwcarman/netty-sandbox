package com.carmanconsulting.netty.actors

import akka.actor.{Props, Actor}
import akka.event.LoggingReceive
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import io.netty.handler.codec.http.HttpHeaders.Names._
import com.carmanconsulting.netty.messages.HttpRequestMessage

class PlainTextResponder(message: String) extends Actor {
  override def receive: Actor.Receive = LoggingReceive {
    case req: HttpRequestMessage =>
      val response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(message, CharsetUtil.UTF_8))
      response.headers.set(CONTENT_TYPE, "text/plain")
      response.headers.set(CONTENT_LENGTH, response.content.readableBytes)
      sender ! response
  }
}

object PlainTextResponder {
  def props(message: String) = Props(classOf[PlainTextResponder], message)
}
