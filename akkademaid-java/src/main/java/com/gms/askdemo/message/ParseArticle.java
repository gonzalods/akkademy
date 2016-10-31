package com.gms.askdemo.message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ParseArticle implements Serializable{

	public final String uri;

	public ParseArticle(String uri) {
		super();
		this.uri = uri;
	}
	
}
