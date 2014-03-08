package com.carmanconsulting.netty

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import com.typesafe.scalalogging.slf4j.Logging

abstract class HttpHandler extends SimpleChannelInboundHandler[HttpObject] with Logging {

  override def channelRead0(ctx: ChannelHandlerContext, httpObject: HttpObject): Unit = {
    httpObject match {
      case request: FullHttpRequest =>
        logger.debug("Received FullHttpRequest\n{}\n", request)
        onFullHttpRequest(ctx, request)
      case request: HttpRequest =>
        logger.debug("Received HttpRequest\n{}\n", request)
        onHttpRequest(ctx, request)
      case last: LastHttpContent =>
        logger.debug("Received LastHttpContent\n{}\n", last)
        onLastHttpContent(ctx, last)
      case content: HttpContent =>
        logger.debug("Received HttpContent\n{}\n", content)
        onHttpContent(ctx, content)
      case _ =>
        logger.error("Received unsupported HttpObject\n{}\n", httpObject)
        onUnhandledHttpObject(ctx, httpObject)
    }
  }

  def onUnhandledHttpObject(ctx: ChannelHandlerContext, httpObject: HttpObject) {
  }

  def onLastHttpContent(ctx: ChannelHandlerContext, last: LastHttpContent) {
  }

  def onHttpContent(ctx: ChannelHandlerContext, content: HttpContent) {
  }

  def onHttpRequest(ctx: ChannelHandlerContext, request: HttpRequest) {
  }

  def onFullHttpRequest(ctx: ChannelHandlerContext, request: FullHttpRequest) {
  }
}
