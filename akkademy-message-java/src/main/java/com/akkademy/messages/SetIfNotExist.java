package com.akkademy.messages;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SetIfNotExist implements Serializable{
	
	public final String key;
	public final Object value;
	public SetIfNotExist(String key, Object value) {
		this.key = key;
		this.value = value;
	}
	
}
