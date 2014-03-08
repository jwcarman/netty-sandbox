package com.carmanconsulting.netty

import akka.actor.ActorRef
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import com.carmanconsulting.netty.messages.NettyHttpMessage
import com.carmanconsulting.netty.util.HttpRequestMessageBuilder

class AkkaHttpHandler(recipient: ActorRef) extends HttpHandler {

  var builder: HttpRequestMessageBuilder = null

  override def onHttpRequest(ctx: ChannelHandlerContext, request: HttpRequest): Unit = {
    builder = new HttpRequestMessageBuilder(request)
  }

  override def onHttpContent(ctx: ChannelHandlerContext, content: HttpContent): Unit = {
    builder.onContent(content)
  }

  override def onLastHttpContent(ctx: ChannelHandlerContext, last: LastHttpContent): Unit = {
    builder.onContent(last)
    recipient ! new NettyHttpMessage(ctx, builder.build())
    builder = null
  }
}

object AkkaHttpHandler {
  def apply(recipient: ActorRef) = new AkkaHttpHandler(recipient)
}
