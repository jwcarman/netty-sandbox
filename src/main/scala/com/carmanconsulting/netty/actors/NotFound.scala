package com.carmanconsulting.netty.actors

import akka.actor.Actor
import akka.event.LoggingReceive
import io.netty.handler.codec.http.{DefaultFullHttpResponse, HttpRequest}
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http.HttpResponseStatus._

class NotFound extends Actor {
  override def receive: Actor.Receive = LoggingReceive {
    case req: HttpRequest =>
      sender ! new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND)
  }
}