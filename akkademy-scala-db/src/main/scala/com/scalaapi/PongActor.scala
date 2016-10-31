package com.scalaapi

import akka.actor.Actor
import akka.actor.Status

class PongActor extends Actor{
  override def receive: Receive = {
    case "Ping" => sender() ! "Pong"
    case _ => sender() ! Status.Failure(new Exception("unkown message"))
  }
}