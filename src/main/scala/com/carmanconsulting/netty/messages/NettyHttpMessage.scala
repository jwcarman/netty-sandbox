package com.carmanconsulting.netty.messages

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpRequest

case class NettyHttpMessage(context:ChannelHandlerContext, request:HttpRequestMessage) {

}
