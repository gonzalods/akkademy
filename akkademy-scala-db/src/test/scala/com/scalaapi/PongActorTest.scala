package com.scalaapi

import org.scalatest.FunSpecLike
import org.scalatest.Matchers
import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.duration._ //para second/seconds
import akka.actor.Props
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.Future
import scala.language.postfixOps



class PongActorTest  extends FunSpecLike with Matchers{
  val system = ActorSystem()
  implicit val timeout = Timeout(5 seconds)
  val pongActor = system.actorOf(Props.create(classOf[PongActor]))
  
  describe("Pong actor"){
    it("debe responder con Pong"){
      val future = pongActor ? "Ping" //Utiliza el timeout implícito
      
      // - Con Await se bloquea el test hasta que se tiene el resultado
      //   disponible
      // - El actor es untyped, asi que se devuelve un Future[AnyRef]
      //   Se llama a future.mapTo[String] para cambiar el tipo de futuro
      //   al tipo esperado de resultado.
      val result = Await.result(future.mapTo[String], 1 second) 
      assert(result == "Pong")
    }
    it("debe fallar en mensaje desconocido"){
      val future = pongActor ? "Desconocido"
      intercept[Exception]{
        Await.result(future.mapTo[String], 1 second)
      }
    }
  }
  
  describe("Ejecución código"){
    import scala.concurrent.ExecutionContext.Implicits.global
    it("debe imprimir en consola"){
      //onSuccess toma como argumento una función parcial.
      //Permite ejecutar código una vez el resultado esta disponible
      askPong("Ping").onSuccess({
        case x: String => println("replied with: " + x)
      })
      Thread.sleep(1000)
    }
  }
  
  describe("transforma"){
    import scala.concurrent.ExecutionContext.Implicits.global
    it("Debe devolver el primer caracter"){
      val future = askPong("Ping").map { x => x.charAt(0)}
      val result = Await.result(future.mapTo[Character], 1 second)
      assert(result == 'P')
    }
  }
  
  describe("transforma asíncrona"){
    import scala.concurrent.ExecutionContext.Implicits.global
    it("Replica de nuevo con Ping"){
      val future: Future[String] = askPong("Ping").flatMap { x => askPong("Ping") }
      val result = Await.result(future.mapTo[String], 1 second)
      assert(result == "Pong")
    }
  }
  
  describe("Ejecución código en fallo"){
    import scala.concurrent.ExecutionContext.Implicits.global
    it("Imprime por consola el fallo"){
      askPong("ErrorProvocado").onFailure{
          case e: Exception => println("Recivida excepción")
      }
    }
  }
  
  describe("Recuperacion de Fallo"){
    import scala.concurrent.ExecutionContext.Implicits.global
    it("retorna default"){
      val future:Future[String] = askPong("ErrorProvocado").recover {
        case t:Exception => "default"
      }
      val result = Await.result(future.mapTo[String], 1 second)
      assert(result == "default")
    }
  }
  
  describe("Recuperacion de Fallo Asincorona"){
    import scala.concurrent.ExecutionContext.Implicits.global
    it("Rellamada con Ping"){
      val future: Future[String] = askPong("ErrorProvocado").recoverWith { 
        case t:Exception => askPong("Ping") 
      }
      val result = Await.result(future.mapTo[String], 1 second)
      assert(result == "Pong") 
    }
  }
  
  describe("Encadenamiento de operaciones"){
    it("retorna default"){
      import scala.concurrent.ExecutionContext.Implicits.global
      val future: Future[String] = askPong("Ping")
              .flatMap( { x => askPong("Ping" + x) })
              .recover({case e: Exception => "default"})
      val result = Await.result(future.mapTo[String], 1 second)
      assert(result == "default")
    }
  }
  
  describe("Lista de Futuros"){
    import scala.concurrent.ExecutionContext.Implicits.global
    it("futuros"){
      
    }
  }
  
  def askPong(message: String): Future[String] = (pongActor ? message).mapTo[String] 
  
}