package com.akkademy.messages;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GetRequest implements Serializable {
	public final String key;
	public GetRequest(String key) {
		super();
		this.key = key;
	}
	
}
