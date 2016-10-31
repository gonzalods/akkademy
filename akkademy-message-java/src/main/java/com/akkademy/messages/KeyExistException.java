package com.akkademy.messages;

import java.io.Serializable;

@SuppressWarnings("serial")
public class KeyExistException extends Exception implements Serializable {
	public final String key;
	public KeyExistException(String key) {
		super();
		this.key = key;
	}
	
}
