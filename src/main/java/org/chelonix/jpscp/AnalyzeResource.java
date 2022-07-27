package org.chelonix.jpscp;

import org.chelonix.jpscp.shared.Article;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/analyze")
public class AnalyzeResource {
    @Inject
    FetchService fetchService;

    @Inject
    AnalyzeService analyzeService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public String analyze(@QueryParam("id") String id) {
        Article article = id != null ? fetchService.fetch(id) : fetchService.fetch();
        if (article == null) {
            throw new NotFoundException("Article not found");
        }
        StringBuilder sb = new StringBuilder();
        article.paragraphs.stream().flatMap(p -> p.stream()).forEach(s -> {
            sb.append(s.rawString()).append("\n");
            sb.append(analyzeService.analyze(s)).append("\n\n");
        });
        return sb.toString();
    }

}
