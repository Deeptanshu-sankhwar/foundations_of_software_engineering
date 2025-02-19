package io.collective.endpoints;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.collective.articles.ArticleDataGateway;
import io.collective.articles.ArticleRecord;
import io.collective.restsupport.RestTemplate;
import io.collective.rss.RSS;  // Use the correct package
import io.collective.workflow.Worker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EndpointWorker implements Worker<EndpointTask> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate template;
    private final ArticleDataGateway gateway;
    private final XmlMapper xmlMapper = new XmlMapper(); // XML parser

    public EndpointWorker(RestTemplate template, ArticleDataGateway gateway) {
        this.template = template;
        this.gateway = gateway;
    }

    @NotNull
    @Override
    public String getName() {
        return "ready";
    }

    @Override
    public void execute(EndpointTask task) throws IOException {
        String response = template.get(task.getEndpoint(), task.getAccept());
        gateway.clear(); // Clear previous data

        // Parse the RSS XML response using the existing RSS model
        RSS rss = xmlMapper.readValue(response, RSS.class);

        // Fix: Use getItem() instead of getItems()
        List<ArticleRecord> articles = rss.getChannel().getItem().stream()
                .map(item -> new ArticleRecord(item.hashCode(), item.getTitle(), true))
                .collect(Collectors.toList());

        // Save articles
        articles.forEach(article -> gateway.save(article.getTitle()));
    }
}
