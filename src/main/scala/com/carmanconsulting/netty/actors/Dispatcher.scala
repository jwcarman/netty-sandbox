package com.carmanconsulting.netty.actors

import akka.actor.{ActorRef, Props, Actor}
import akka.event.LoggingReceive
import com.carmanconsulting.netty.messages.NettyHttpMessage
import io.netty.handler.codec.http.QueryStringDecoder

class Dispatcher extends Actor {

  //TODO: create "real" dispatching logic!
  var handlers = collection.mutable.Map[String,ActorRef]().withDefaultValue(context.actorOf(Props[NotFound]))
  handlers("/") = context.actorOf(Props[Hello])
  handlers("/foo") = context.actorOf(PlainTextResponder.props("bar"))
  handlers("/timeout") = context.actorOf(Props[DelayedResponder])

  override def receive: Actor.Receive = LoggingReceive {
    case message: NettyHttpMessage =>
      val responseWriter = context.actorOf(Props(classOf[ResponseWriter], message.context))
      val decoder: QueryStringDecoder = new QueryStringDecoder(message.request.path)
      val handler = handlers(decoder.path())
      handler.tell(message.request, responseWriter)
  }
}
