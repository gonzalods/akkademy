package com.gms.askdemo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;











import com.akkademy.messages.GetRequest;
import com.akkademy.messages.SetRequest;
import com.gms.askdemo.message.ArticleBody;
import com.gms.askdemo.message.HttpResponse;
import com.gms.askdemo.message.ParseArticle;




import com.gms.askdemo.message.ParseHtmlArticle;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Status;
import akka.japi.pf.ReceiveBuilder;
import akka.util.Timeout;
import static akka.pattern.Patterns.ask;
import static scala.compat.java8.FutureConverters.toJava;

public class AskDemoArticleParser extends AbstractActor {
	
	private final ActorSelection cacheActor;
	private final ActorSelection httpClientActor;
	private final ActorSelection articleParseActor;
	private final Timeout timeout;
	
	public AskDemoArticleParser(String cacheActorPath,
			String httpClientActorPath, String articleParseActorPath,
			Timeout timeout) {
		this.cacheActor = context().system().actorSelection(cacheActorPath);
		this.httpClientActor = context().system().actorSelection(httpClientActorPath);
		this.articleParseActor = context().system().actorSelection(articleParseActorPath);
		this.timeout = timeout;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PartialFunction<Object, BoxedUnit> receive() {
		return ReceiveBuilder
				.match(ParseArticle.class, msg -> {
					final CompletionStage cacheResult = toJava(ask(cacheActor, new GetRequest(msg.uri), timeout));
					final CompletionStage result = cacheResult.handle((x, t) -> {
						return (x != null)
								? CompletableFuture.completedFuture(x)
								: toJava(ask(httpClientActor, msg.uri, timeout))
									.thenCompose(rawArticle -> toJava(
											ask(articleParseActor,
													new ParseHtmlArticle(msg.uri, 
															((HttpResponse)rawArticle).rawArticle), timeout))
								);
					}).thenCompose(x -> x);
					
					final ActorRef senderRef = sender();
					result.handle((x,t) -> {
						if(x != null){
							if(x instanceof ArticleBody){
								String body = ((ArticleBody)x).body;
								cacheActor.tell(new SetRequest(msg.uri, body), self());
								senderRef.tell(body, self());
							}else if(x instanceof String){
								senderRef.tell(x, self());
							}
						}else if(x == null)
							senderRef.tell(new Status.Failure((Throwable)t), self());
						return null;
					});
				}).build();
	}
}
