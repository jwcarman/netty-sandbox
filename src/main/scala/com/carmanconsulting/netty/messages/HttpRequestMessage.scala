package com.carmanconsulting.netty.messages

class HttpRequestMessage(val path: String,
                         val headers: Map[String, String],
                         val parameters: Map[String, List[String]]) {

  /**
   * Returns the first (or only) value of the named parameter
   * @param name the parameter name
   * @return
   */
  def parameter(name: String): Option[String] = {
    val values = parameters(name)
    values match {
      case Nil => None
      case first :: rest => Option(first)
    }
  }
}
