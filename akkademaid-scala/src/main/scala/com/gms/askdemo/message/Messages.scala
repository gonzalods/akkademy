package com.gms.askdemo.message

case class ParseArticle(uri:String)
case class ParseHtmlArticle(uri:String,rawArticle:String)
case class HttpResponse(rawArticle:String)
case class ArticleBody(body:String)