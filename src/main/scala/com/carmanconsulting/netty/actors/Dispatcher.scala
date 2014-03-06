package com.carmanconsulting.netty.actors

import akka.actor.{ActorRef, Props, Actor}
import akka.event.LoggingReceive
import com.carmanconsulting.netty.messages.NettyHttpMessage

class Dispatcher extends Actor {

  //TODO: create "real" dispatching logic!
  var handlers = collection.mutable.Map[String,ActorRef]().withDefaultValue(context.actorOf(Props[NotFound]))
  handlers("/") = context.actorOf(Props[Hello])
  handlers("/foo") = context.actorOf(PlainTextResponder.props("bar"))
  handlers("/timeout") = context.actorOf(Props[DelayedResponder])

  override def receive: Actor.Receive = LoggingReceive {
    case req: NettyHttpMessage =>
      val responseWriter = context.actorOf(Props(classOf[ResponseWriter], req.context))
      val handler = handlers(req.request.getUri)
      handler.tell(req.request, responseWriter)
  }
}
