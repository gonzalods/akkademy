package com.akkademy;

import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import com.akkademy.messages.DeleteRequest;
import com.akkademy.messages.GetRequest;
import com.akkademy.messages.KeyExistException;
import com.akkademy.messages.KeyNotFoundException;
import com.akkademy.messages.SetIfNotExist;
import com.akkademy.messages.SetRequest;

public class AkkademyDb extends AbstractActor {
	
	protected final LoggingAdapter log = Logging.getLogger(context().system(), this);
	protected final Map<String, Object> map = new HashMap<>();
	
	private AkkademyDb(){
		receive(ReceiveBuilder
			.match(SetRequest.class, message -> {
				log.info("Received Set request - key: {} value: {}",message.key, message.value);
				map.put(message.key, message.value);
				sender().tell(new Status.Success(message.key), self());
			})
			.match(GetRequest.class, message -> {
				log.info("Received Get request - key: {} ",message.key);
				Object value = map.get(message.key);
				Object response = (value != null)
						? value 
						: new Status.Failure(new KeyNotFoundException(message.key));
				sender().tell(response, self());
			})
			.match(SetIfNotExist.class, message -> {
				log.info("Received SetIfExist request - key: {} value: {}",message.key, message.value);
				Object value = map.get(message.key);
				Object response;
				if(value != null){
					response = new Status.Failure(new KeyExistException(message.key)); 
				}else{
					map.put(message.key, message.value);
					response = new Status.Success(message.key);
				}
				sender().tell(response, self());
			})
			.match(DeleteRequest.class, message -> {
				log.info("Received Delete request - key: {}",message.key);
				Object value = map.remove(message.key);
				Object response = (value != null)
						? new Status.Success(message.key) 
						: new Status.Failure(new KeyNotFoundException(message.key));
				sender().tell(response, self());
			})
			.matchAny(o -> sender().tell(new Status.Failure(new ClassNotFoundException()), self()))
			.build());
	}
}
