package com.gms.askdemo

import akka.util.Timeout
import akka.actor.Actor
import com.gms.askdemo.message.ParseArticle
import com.akkademy.message.GetRequest
import akka.pattern.ask;
import com.gms.askdemo.message.HttpResponse
import com.gms.askdemo.message.ParseHtmlArticle
import scala.concurrent.Future
import scala.util.Success
import com.gms.askdemo.message.ArticleBody
import com.akkademy.message.SetRequest
import scala.util.Failure
import akka.actor.Status

class AskDemoArticleParser(cacheActorPath: String, httpClientActorPath: String,
    articleParserActorPath: String, implicit val timeout:Timeout) extends Actor{
  
  val cacheActor = context.actorSelection(cacheActorPath)
  val httpClientActor = context.actorSelection(httpClientActorPath)
  val articleParserActor = context.actorSelection(articleParserActorPath)
  import scala.concurrent.ExecutionContext.Implicits.global
  
  override def receive: Receive = {
    case ParseArticle(uri) => 
      val senderRef = sender()
      val cacheResult = cacheActor ? GetRequest(uri)
      val result = cacheResult.recoverWith{
        case _:Exception => 
          val fRawResult = httpClientActor ? uri
          fRawResult flatMap {
            case HttpResponse(rawArticle) => 
              articleParserActor ? ParseHtmlArticle(uri, rawArticle)
            case x =>
              Future.failed(new Exception("unknown response"))
          }
      }
      
      result onComplete { 
        case Success(x: String) =>
          println("cached result!")
          senderRef ! x
        case Success(x: ArticleBody) =>
          cacheActor ! SetRequest(uri, x.body)
          senderRef ! x
        case Failure(t) => 
          senderRef ! Status.Failure(t)
        case x =>
          println("unknown message! " + x)
      }
  }
}