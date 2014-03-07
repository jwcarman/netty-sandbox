package com.carmanconsulting.netty

import akka.actor.ActorRef
import io.netty.channel.{SimpleChannelInboundHandler, ChannelHandlerContext}
import io.netty.handler.codec.http.FullHttpRequest
import org.slf4j.LoggerFactory
import com.carmanconsulting.netty.messages.NettyHttpMessage

class AkkaHttpHandler(dispatcher: ActorRef) extends SimpleChannelInboundHandler[FullHttpRequest] {
  val logger = LoggerFactory.getLogger(classOf[AkkaHttpHandler])


  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  override def channelRead0(ctx: ChannelHandlerContext, req: FullHttpRequest): Unit = {
    logger.info("Received FullHttpRequest")
    dispatcher ! new NettyHttpMessage(ctx, req)
  }
}
