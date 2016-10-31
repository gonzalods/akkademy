package com.gms.askdemo.message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HttpResponse implements Serializable {
	public final String rawArticle;

	public HttpResponse(String rawArticle) {
		super();
		this.rawArticle = rawArticle;
	}
	
}
