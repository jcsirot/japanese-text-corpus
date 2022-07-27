package org.chelonix.jpscp;

import org.chelonix.jpscp.shared.Article;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/fetch")
public class FetchResource {

    @Inject
    FetchService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public String fetch(@QueryParam("id") String id, @QueryParam("readings") boolean readings) {
        Article article = id != null ? service.fetch(id) : service.fetch();
        if (article == null) {
            throw new NotFoundException("Article not found");
        }
        return formatArticle(article, readings);
    }

    private String formatArticle(Article article, boolean readings) {
        StringBuilder sb = new StringBuilder();
        sb.append(article.getTitle()).append("\n").append("===").append("\n\n");
        for (Article.Paragraph paragraph: article.paragraphs) {
            for (Article.Sentence sentence: paragraph) {
                for (Article.Token token: sentence) {
                    switch (token) {
                        case Article.TextToken t -> sb.append(t.text());
                        case Article.RubyToken r -> {
                            sb.append(r.text());
                            if (readings) {
                                sb.append("[").append(r.reading()).append("]");
                            }
                        }
                        default -> throw new IllegalArgumentException("Unrecognized shape");
                    };
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}