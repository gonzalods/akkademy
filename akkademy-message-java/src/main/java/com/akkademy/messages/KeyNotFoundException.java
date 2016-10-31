package com.akkademy.messages;

import java.io.Serializable;

@SuppressWarnings("serial")
public class KeyNotFoundException extends Exception implements Serializable {
	public final String key;
	public KeyNotFoundException(String key) {
		super();
		this.key = key;
	}
}
