package com.carmanconsulting.netty

import akka.actor.{ActorSystem, Props}
import io.netty.channel.{SimpleChannelInboundHandler, ChannelHandlerContext}
import io.netty.handler.codec.http.FullHttpRequest
import org.slf4j.LoggerFactory
import com.carmanconsulting.netty.actors.Dispatcher
import com.carmanconsulting.netty.messages.NettyHttpMessage
import io.netty.channel.ChannelHandler.Sharable

@Sharable
class AkkaHttpHandler extends SimpleChannelInboundHandler[FullHttpRequest] {
  val logger = LoggerFactory.getLogger(classOf[AkkaHttpHandler])
  val system = ActorSystem("http-server")
  val dispatcher = system.actorOf(Props[Dispatcher])

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  override def channelRead0(ctx: ChannelHandlerContext, req: FullHttpRequest): Unit = {
    logger.info("Received FullHttpRequest")
    dispatcher ! new NettyHttpMessage(ctx, req)
  }
}
