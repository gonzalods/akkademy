package com.akkademy

import akka.actor.Actor
import scala.collection.mutable.HashMap
import akka.event.Logging
import com.akkademy.message.SetRequest
import akka.actor.Status
import com.akkademy.message.GetRequest
import com.akkademy.message.KeyNotFoundException
import akka.actor.ActorSystem
import akka.actor.Props
import com.akkademy.message.SetIfNoExist
import com.akkademy.message.KeyExistException
import com.akkademy.message.DeleteRequest

class AkkademyDb extends Actor{
  
  val map = new HashMap[String, Object]
  val log = Logging(context.system, this)
  
  override def receive = {
    case SetRequest(key, value) => {
      log.info("Received SetRequest - key: {} value: {}", key, value)
      map.put(key, value)
      sender ! Status.Success
    }
    case GetRequest(key) => {
      log.info("Received GetRequest - key: {} ", key)
      val response: Option[Object] = map.get(key)
      response match{
        case Some(x) => sender() ! x
        case None =>  sender()  ! Status.Failure(new KeyNotFoundException(key))
      }
    }
    case SetIfNoExist(key, value) => {
      log.info("Received SetIfNoExist - key: {} value: {}", key, value)
      val response: Option[Object] = map.get(key)
      response match {
        case Some(x) => sender() ! Status.Failure(new KeyExistException(key))
        case None => {
          map.put(key, value)
          sender() ! Status.Success
        }
      }
    }
    case DeleteRequest(key) => {
      log.info("Received DeleteRequest - key: {}", key)
      val response: Option[Object] = map.remove(key)
      response match{
        case Some(x) => sender() ! Status.Success
        case None => sender() ! Status.Failure(new KeyNotFoundException(key))
      }
    }
    case o => Status.Failure(new ClassNotFoundException)
  }
}

object Main extends App{
  val system = ActorSystem("akkademy")
  system.actorOf(Props[AkkademyDb], name = "akkademy-db")
}