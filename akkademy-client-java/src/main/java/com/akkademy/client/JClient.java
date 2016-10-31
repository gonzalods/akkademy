package com.akkademy.client;

import static akka.pattern.Patterns.ask;
import static scala.compat.java8.FutureConverters.toJava;

import java.util.concurrent.CompletionStage;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;

import com.akkademy.messages.DeleteRequest;
import com.akkademy.messages.GetRequest;
import com.akkademy.messages.SetIfNotExist;
import com.akkademy.messages.SetRequest;

public class JClient {
	private final ActorSystem system = ActorSystem.create("LocalSystem");
	private final ActorSelection remoteDb;
	
	public JClient(String remoteAddress){
		remoteDb = system.actorSelection("akka.tcp://akkademy@" + remoteAddress + "/user/akkademy-db");
	}
	public CompletionStage<?> set(String key, Object value){
		return toJava(ask(remoteDb,new SetRequest(key, value), 2000));
	}
	public CompletionStage<Object> get(String key){
		return toJava(ask(remoteDb, new GetRequest(key), 2000));
	}
	public CompletionStage<Object> setIfNotExist(String key, Object value){
		return toJava(ask(remoteDb,new SetIfNotExist(key,value), 2000));
	}
	public CompletionStage<Object> delete(String key){
		return toJava(ask(remoteDb, new DeleteRequest(key), 2000));
	}
}
