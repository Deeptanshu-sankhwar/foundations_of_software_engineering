package io.collective.start;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.collective.articles.ArticleDataGateway;
import io.collective.articles.ArticleRecord;
import io.collective.articles.ArticlesController;
import io.collective.endpoints.EndpointDataGateway;
import io.collective.endpoints.EndpointTask;
import io.collective.endpoints.EndpointWorkFinder;
import io.collective.endpoints.EndpointWorker;
import io.collective.restsupport.BasicApp;
import io.collective.restsupport.NoopController;
import io.collective.restsupport.RestTemplate;
import io.collective.workflow.WorkScheduler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TimeZone;

public class App extends BasicApp {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final ArticleDataGateway articleDataGateway = new ArticleDataGateway(List.of(
            new ArticleRecord(10101, "Programming Languages InfoQ Trends Report - October 2019 4", true),
            new ArticleRecord(10106, "Ryan Kitchens on Learning from Incidents at Netflix, the Role of SRE, and Sociotechnical Systems", true)
    ));

    private final WorkScheduler<EndpointTask> scheduler;

    public App(int port) {
        super(port);

        // Initialize WorkFinder and Worker
        EndpointWorkFinder workFinder = new EndpointWorkFinder(new EndpointDataGateway());
        EndpointWorker worker = new EndpointWorker(new RestTemplate(), articleDataGateway);

        // Create WorkScheduler
        this.scheduler = new WorkScheduler<>(workFinder, List.of(worker), 10L);
    }

    @Override
    public void start() {
        super.start();
        logger.info("Starting WorkScheduler...");
        scheduler.start();  // Start the background worker
    }

    @NotNull
    @Override
    protected HandlerList handlerList() {
        HandlerList list = new HandlerList();
        list.addHandler(new ArticlesController(new ObjectMapper(), articleDataGateway));
        list.addHandler(new NoopController());
        return list;
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        String port = System.getenv("PORT") != null ? System.getenv("PORT") : "8881";
        App app = new App(Integer.parseInt(port));

        // Add a shutdown hook to clean up resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down WorkScheduler...");
            app.scheduler.shutdown();
        }));

        app.start();
    }
}
