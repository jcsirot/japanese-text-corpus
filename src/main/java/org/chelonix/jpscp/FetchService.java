package org.chelonix.jpscp;

import org.chelonix.jpscp.nhknewseasy.NewsService;
import org.chelonix.jpscp.shared.Article;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@ApplicationScoped
public class FetchService {
    @Inject
    NewsService service;

    public Article fetch() {
        Stream<Article> articles = service.getArticles();
        return articles.findFirst().orElse(null);
    }

    public Article fetch(String id) {
        return service.getArticle(id);
    }
}
