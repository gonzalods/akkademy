package com.akkademy.messages;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DeleteRequest implements Serializable {

	public final String key;

	public DeleteRequest(String key) {
		this.key = key;
	}
	
}
