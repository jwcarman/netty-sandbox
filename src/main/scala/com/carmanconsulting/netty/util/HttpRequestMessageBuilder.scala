package com.carmanconsulting.netty.util

import io.netty.handler.codec.http._
import com.carmanconsulting.netty.messages.HttpRequestMessage
import scala.collection.JavaConversions._
import java.util

class HttpRequestMessageBuilder(request: HttpRequest) {
  val queryDecoder = new QueryStringDecoder(request.getUri)
  val parameters: Map[String, List[String]] = parseParameters()
  var headers: Map[String, String] = request.headers()

  def onContent(content: HttpContent): HttpRequestMessageBuilder = {
    this
  }

  implicit def headersToMap(headers:HttpHeaders): Map[String,String] = {
    var map:collection.mutable.Map[String,String] = collection.mutable.Map()
    map ++= headers.iterator().map(entry => (entry.getKey,entry.getValue))
    map.toMap
  }

  def parseParameters():Map[String,List[String]] = {
    val original: collection.mutable.Map[String, util.List[String]] = new QueryStringDecoder(request.getUri).parameters()
    original.mapValues(_.toList).toMap
  }

  def onLastContent(content: LastHttpContent): HttpRequestMessageBuilder = {
    headers ++= content.trailingHeaders()
    this
  }

  def build(): HttpRequestMessage = {
    new HttpRequestMessage(queryDecoder.path(), headers, parameters)
  }
}
