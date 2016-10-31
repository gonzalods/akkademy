package com.gms.telldemo;

import java.util.concurrent.TimeoutException;

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
import akka.actor.Props;
import akka.actor.Status;
import akka.japi.pf.ReceiveBuilder;
import akka.util.Timeout;

public class TellDemoArticleParser extends AbstractActor {

	private final ActorSelection cacheActor;
	private final ActorSelection httpClientActor;
	private final ActorSelection articleParseActor;
	private final Timeout timeout;
	
	public TellDemoArticleParser(String cacheActorPath,
			String httpClientActorPath, String articleParseActorPath,
			Timeout timeout) {
		super();
		this.cacheActor = context().system().actorSelection(cacheActorPath);
		this.httpClientActor = context().system().actorSelection(httpClientActorPath);
		this.articleParseActor = context().system().actorSelection(articleParseActorPath);
		this.timeout = timeout;
	}

	@Override
	public PartialFunction<Object, BoxedUnit> receive() {
		return ReceiveBuilder
				.match(ParseArticle.class, msg -> {
					ActorRef extraActor = buildExtraActor(sender(), msg.uri);
					cacheActor.tell(new GetRequest(msg.uri), extraActor);
					httpClientActor.tell(msg.uri, extraActor);
					context().system().scheduler().scheduleOnce(timeout.duration(), extraActor,
							"timeout", context().system().dispatcher(), ActorRef.noSender());
				})
				.build();
	}

	private ActorRef buildExtraActor(ActorRef senderRef, String uri) {
		class MyActor extends AbstractActor{
			public MyActor() {
				receive(ReceiveBuilder
						.matchEquals(String.class, x -> x.equals("timeout"), x -> {//disparo de timeout del scheluder
							senderRef.tell(new Status.Failure(new TimeoutException("timeout!")), self());
							context().stop(self());
						})
						.match(HttpResponse.class, httpResponse -> {//Respuesta de httpClienteActor
							articleParseActor.tell(new ParseHtmlArticle(uri, httpResponse.rawArticle), self());
						})
						.match(String.class, body -> {// Respuesta de cacheActor
							senderRef.tell(body, self());
							context().stop(self());
						})
						.match(ArticleBody.class, articleBody -> {//Respuesta de articleParseActor
							cacheActor.tell(new SetRequest(uri, articleBody.body), self());
							senderRef.tell(articleBody.body, self());
							context().stop(self());
						})
						.matchAny(t -> {
							System.out.println("ignoring msg: " + t.getClass());
						})
						.build()
				);
			}
			
		}
		return context().system().actorOf(Props.create(MyActor.class, () -> new MyActor()));
	}
	
}
