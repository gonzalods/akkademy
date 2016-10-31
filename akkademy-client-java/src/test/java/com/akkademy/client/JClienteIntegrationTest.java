package com.akkademy.client;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.concurrent.CompletableFuture;


import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.akkademy.client.JClient;
import com.akkademy.messages.KeyExistException;
import com.akkademy.messages.KeyNotFoundException;

public class JClienteIntegrationTest {

	JClient client = new JClient("127.0.0.1:2552");
	
//	@Before
//	public void ini(){
//		client 
//	}
	@Test
	public void akkademyDbTest() throws Exception {
		
		client.set("123", 123);
		Integer intResult = (Integer)((CompletableFuture<Object>)client.get("123")).get();
		assertEquals("debeEstablecerUnRegistro",new Integer(123), intResult);

		CompletableFuture<Object> jFuture = ((CompletableFuture<Object>)client.get("321"))
				.exceptionally(t -> {
					return String.format("No existe la clave %s", ((KeyNotFoundException)t).key);
				});
		String strResult = (String)jFuture.get(2000, TimeUnit.MILLISECONDS);
		assertThat("Debe devolver un error NoExisteClave", strResult, equalTo("No existe la clave 321"));
		
		client.setIfNotExist("321", 321);
		intResult = (Integer)((CompletableFuture<Object>)client.get("321")).get(2000,TimeUnit.MILLISECONDS);
		assertEquals("Establecer dato con Clave no existente",new Integer(321), intResult);
		
		jFuture = ((CompletableFuture<Object>) client.setIfNotExist("321", 321))
				.exceptionally(t -> {
					return String.format("Ya existe un registro con clave %s", ((KeyExistException)t).key);
				});
		strResult = (String)jFuture.get(2000, TimeUnit.MILLISECONDS);
		assertThat("Error con Clave ya existente", strResult, equalTo("Ya existe un registro con clave 321"));
		
		jFuture = (CompletableFuture<Object>)client.delete("321");
		strResult = (String)jFuture.get(2000, TimeUnit.MILLISECONDS);
		assertThat("Borra correctamente un Clave existente",strResult, equalTo("321"));
		
		jFuture = ((CompletableFuture<Object>)client.delete("321")).exceptionally(t -> {
			return String.format("No existe la clave %s", ((KeyNotFoundException)t).key);
		});
		strResult = (String)jFuture.get(2000, TimeUnit.MILLISECONDS);
		assertThat("Error al Borrar un Clave no existente",strResult, equalTo("No existe la clave 321"));
		
	}
}
