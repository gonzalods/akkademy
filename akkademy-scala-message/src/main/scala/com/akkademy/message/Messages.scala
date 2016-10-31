package com.akkademy.message

/*
 * Las case class son
 * - Inmutables por defecto. Atributos de tipo val por defecto.
 * - Descomponibles mediante pattern matching
 * - Comparables por igualdad estructural en lugar de por referencia
 * - Suscintas para instanciar y operar en ellas
 */
//las clases de tipo case son serializables
case class SetRequest(key: String, value: Object) 
case class GetRequest(key: String)
case class SetIfNoExist(key:String, value:Object)
case class DeleteRequest(key:String)
case class KeyNotFoundException(key: String) extends Exception
case class KeyExistException(key: String) extends Exception
