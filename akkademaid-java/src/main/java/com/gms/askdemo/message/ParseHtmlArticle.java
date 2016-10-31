package com.gms.askdemo.message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ParseHtmlArticle implements Serializable {
	public final String uri;
	public final String rawArticle;
	public ParseHtmlArticle(String uri, String rawArticle) {
		super();
		this.uri = uri;
		this.rawArticle = rawArticle;
	}
	
}
