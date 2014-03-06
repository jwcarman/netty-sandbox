package com.carmanconsulting.netty.messages

import akka.actor.ActorRef

case class DelayedResponse(receiver: ActorRef)
