package com.gms.askdemo.message;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class ParsingActor extends AbstractActor {

	@Override
	public PartialFunction<Object, BoxedUnit> receive() {
		return ReceiveBuilder
				.match(ParseHtmlArticle.class, html -> {
					String body = ArticleExtractor.INSTANCE.getText(html.rawArticle);
					sender().tell(new ArticleBody(body), self());
				})
				.build();
	}

	
}
