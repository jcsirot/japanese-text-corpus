package org.chelonix.jpscp.nhknewseasy;

import io.quarkus.logging.Log;
import org.chelonix.jpscp.shared.Article;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

@ApplicationScoped
public class NewsService {

    @RestClient
    NewsListFetcher service;

    private final ReentrantLock lock = new ReentrantLock();

    private volatile NewsList newsList;

    public Article getArticle(String id) {
        lock.lock();
        try {
            if (newsList == null) {
                newsList = service.getNewsList();
            }
        } finally {
            lock.unlock();
        }
        return newsList.get(0).values().stream().flatMap(ArrayList::stream)
                .filter(item -> id.equals(item.getId())).mapMulti(this::mapArticle).findFirst().orElse(null);
    }

    public Stream<Article> getArticles() {
        lock.lock();
        try {
            if (newsList == null) {
                newsList = service.getNewsList();
            }
        } finally {
            lock.unlock();
        }
        Stream<NewsItem> items = newsList.get(0).values().stream().flatMap(ArrayList::stream).sorted();
        return items.mapMulti(this::mapArticle);
    }

    private void mapArticle(NewsItem item, Consumer<Article> mapper) {
        try {
            Article a = parse(item);
            mapper.accept(a);
        } catch (IOException ioe) {
            Log.error("Could not fetch article", ioe);
        }
    }

    private static boolean hasRubyParent(Node node) {
        if (node instanceof Element && (((Element) node).tagName().equals("ruby") || ((Element) node).parents().is("ruby"))) {
            return true;
        } else if (!node.hasParent()) {
            return false;
        }
        return hasRubyParent(node.parent());
    }

    private Article parse(NewsItem item) throws IOException {
        String url = String.format("https://www3.nhk.or.jp/news/easy/%1$s/%1$s.html", item.getId());
        Document doc = Jsoup.connect(url).get();
        Log.infov("Article Id {0}", item.getId());
        Article.Builder builder = Article.newBuilder();
        builder.setTitle(item.getTitle());
        Elements newsBody = doc.select(".article-body p");
        newsBody.stream().forEach(elt -> {
            elt.forEachNode(child -> {
                if (child instanceof TextNode && !hasRubyParent(child)) {
                    //Log.infov(((TextNode) child).text());
                    String text = ((TextNode) child).text();
                    while (true) {
                        int idx = text.indexOf("。");
                        if (idx >= 0) {
                            String slice = text.substring(0, idx + 1);
                            text = text.substring(idx + 1);
                            builder.add(new Article.TextToken(slice));
                            builder.newSentence();
                        } else {
                            break;
                        }
                    }
                    if (text.length() > 0) {
                        builder.add(new Article.TextToken(text));
                    }
                } else if (child instanceof Element && ((Element) child).tagName().equals("ruby")) {
                    Element ruby = (Element)child;
                    String text = ruby.ownText();
                    if (text.isBlank()) {
                        text = ruby.getElementsByTag("span").first().ownText();
                    }
                    String reading = ruby.getElementsByTag("rt").text();
                    //Log.infov("ruby: {0}    [{1}]", text, reading);
                    builder.add(new Article.RubyToken(text, reading));
                }
            });
            builder.newParagraph();
        });

        return builder.build();
    }
}
