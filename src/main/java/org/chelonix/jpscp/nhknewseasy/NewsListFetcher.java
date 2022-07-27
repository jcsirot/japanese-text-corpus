package org.chelonix.jpscp.nhknewseasy;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@RegisterRestClient
@Path("/news/easy")
public interface NewsListFetcher {

    @GET
    @Path("/news-list.json")
    NewsList getNewsList();
}
