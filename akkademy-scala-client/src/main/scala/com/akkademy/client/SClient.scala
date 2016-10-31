package com.akkademy.client

import akka.util.Timeout
import scala.concurrent.duration._
import akka.actor.ActorSystem
import com.akkademy.message.SetRequest
import com.akkademy.message.GetRequest
import akka.pattern.ask
import com.akkademy.message.SetIfNoExist
import com.akkademy.message.DeleteRequest
import scala.language.postfixOps

class SClient(remoteAddres:String) {
  private implicit val timeout = Timeout(2 seconds)
  private implicit val system = ActorSystem("LocalSystem")
  private val remoteDb = system.actorSelection(s"akka.tcp://akkademy@$remoteAddres/user/akkademy-db")

  def set(key: String, value: Object) = {
    remoteDb ? SetRequest(key, value)
  }

  def get(key: String) = {
    remoteDb ? GetRequest(key)
  }
  
  def setIfNoExist(key: String, value: Object) = {
    remoteDb ? SetIfNoExist(key, value)
  }
  
  def remove(key: String) = {
    remoteDb ? DeleteRequest(key)
  }
}