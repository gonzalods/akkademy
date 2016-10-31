package com.akkademy.client

import org.scalatest.Matchers
import org.scalatest.FunSpecLike
import scala.concurrent.Await
import scala.concurrent.duration._
import com.akkademy.message.KeyNotFoundException
import com.akkademy.message.KeyExistException
import scala.language.postfixOps

class SClientIntegrationSpec extends FunSpecLike with Matchers{
  val client = new SClient("127.0.0.1:2552")
  
  describe("akkademyDbClient"){
    it("should set a value"){
      client.set("123", new Integer(123))
      val futureResult = client.get("123")
      val result = Await.result(futureResult, 10 seconds)
      result should equal(123)    
    }
    it("Debe devolver un error NoExisteClave"){
      import scala.concurrent.ExecutionContext.Implicits.global
      val futureResult = client.get("321").recover{
        case e:KeyNotFoundException => s"No existe la clave ${e.key}"
      }
      val result = Await.result(futureResult.mapTo[String], 1 second)
      result should equal("No existe la clave 321")
    }
    it("Establecer dato con Clave no existente"){
      client.setIfNoExist("234", new Integer(234))
      val futureResult = client.get("234")
      val result = Await.result(futureResult, 10 seconds)
      result should equal(234)
    }
    it("Error con Clave ya existente"){
      import scala.concurrent.ExecutionContext.Implicits.global
      val futureResult = client.setIfNoExist("234", new Integer(234)).recover{
        case e:KeyExistException => s"Ya existe un registro con clave ${e.key}"    
      }
      val result = Await.result(futureResult, 1 seconds)
      result should equal("Ya existe un registro con clave 234")
    }
    it("Borra correctamente un Clave existente"){
      import scala.concurrent.ExecutionContext.Implicits.global
      client.remove("234")
      val futureResult = client.get("234").recover{
        case e:KeyNotFoundException => s"No existe la clave ${e.key}"
      }
      val result = Await.result(futureResult, 1 seconds)
      result should equal("No existe la clave 234")
    }
    it("Error al Borrar un Clave no existente"){
      import scala.concurrent.ExecutionContext.Implicits.global
      val futureResult = client.remove("234").recover{
        case e:KeyNotFoundException => s"No existe la clave ${e.key}"
      }
      val result = Await.result(futureResult, 1 seconds)
      result should equal("No existe la clave 234")
    }
  }
}