package com.gms.telldemo

import akka.util.Timeout
import akka.actor.Actor
import com.gms.askdemo.message.ParseArticle
import akka.actor.ActorRef
import com.akkademy.message.GetRequest
import akka.actor.Props
import scala.util.Failure
import java.util.concurrent.TimeoutException
import com.gms.askdemo.message.HttpResponse
import com.gms.askdemo.message.ParseHtmlArticle
import com.gms.askdemo.message.ArticleBody
import com.akkademy.message.SetRequest

class TellDemoArticleParser(cacheActorPath: String, httpClientActorPath: String,
    articleParserActorPath: String, implicit val timeout:Timeout) extends Actor {
  
  val cacheActor = context.actorSelection(cacheActorPath)
  val httpClientActor = context.actorSelection(httpClientActorPath)
  val articleParserActor = context.actorSelection(articleParserActorPath)
  import scala.concurrent.ExecutionContext.Implicits.global
  
  override def receive:Receive = {
    case msg @ ParseArticle(uri) =>
      val extraActor = buildExtraActor(sender(), uri)
      
      cacheActor.tell(new GetRequest(msg.uri), extraActor)
      httpClientActor.tell(msg.uri, extraActor)
      context.system.scheduler.scheduleOnce(timeout.duration, extraActor, "timeout")
      
  }
  
  private def buildExtraActor(senderRef: ActorRef, uri:String): ActorRef = {
    return context.actorOf(Props(new Actor{
      override def receive: Receive = {
        case "timeout" => 
          senderRef ! Failure(new TimeoutException("timeout!"))
          context.stop(self)
        case HttpResponse(body) => 
          articleParserActor ! ParseHtmlArticle(uri,body)
        case body: String =>
          senderRef ! body
          context.stop(self)
        case ArticleBody(body) => 
          cacheActor ! new SetRequest(uri,body)
          senderRef ! body
          context.stop(self)
        case t => //Caso en que la cache falla, se supone que
                  //httpClientActor respondera con un documento.
          println("ignorin msg: " + t.getClass)
      }
    }))
  }
}