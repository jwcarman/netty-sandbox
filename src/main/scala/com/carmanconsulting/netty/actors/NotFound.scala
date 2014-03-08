package com.carmanconsulting.netty.actors

import akka.actor.Actor
import akka.event.LoggingReceive
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http.HttpResponseStatus._
import com.carmanconsulting.netty.messages.HttpRequestMessage

class NotFound extends Actor {
  override def receive: Actor.Receive = LoggingReceive {
    case req: HttpRequestMessage =>
      sender ! new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND)
  }
}