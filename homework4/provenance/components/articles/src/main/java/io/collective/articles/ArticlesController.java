package io.collective.articles;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.collective.restsupport.BasicHandler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

public class ArticlesController extends BasicHandler {
    private final ArticleDataGateway gateway;

    public ArticlesController(ObjectMapper mapper, ArticleDataGateway gateway) {
        super(mapper);
        this.gateway = gateway;
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        get("/articles", List.of("application/json", "text/html"), request, servletResponse, () -> {

            // Query all articles from the gateway
            List<ArticleInfo> articles = gateway.findAll().stream()
                    .map(article -> new ArticleInfo(article.getId(), article.getTitle()))
                    .collect(Collectors.toList());

            // Convert to JSON and send response
            writeJsonBody(servletResponse, articles);
        });

        get("/available", List.of("application/json"), request, servletResponse, () -> {

            // Query only available articles from the gateway
            List<ArticleInfo> availableArticles = gateway.findAvailable().stream()
                    .map(article -> new ArticleInfo(article.getId(), article.getTitle()))
                    .collect(Collectors.toList());

            // Convert to JSON and send response
            writeJsonBody(servletResponse, availableArticles);
        });
    }
}
