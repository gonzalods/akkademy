package com.gms.askdemo.message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ArticleBody implements Serializable {
	public final String body;

	public ArticleBody(String body) {
		super();
		this.body = body;
	}
	
}
