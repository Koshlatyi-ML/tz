package app.config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

import static java.net.http.HttpClient.Version.HTTP_2;

@Configuration
public class ClientConfiguration {
    @Value("${api.contextpath}")
    private String contextPath;
    @Value("${server.address}")
    private String serverAddress;
    @Value("${server.port}")
    private int port;

    private String usersRoute;
    private String postsRoute;
    private String commentsRoute;
    private String shutdown;

    @PostConstruct
    public void initRoutes() {
        usersRoute = contextPath + "/users";
        postsRoute = contextPath + "/posts";
        commentsRoute = contextPath + "/comments";
        shutdown = String.format("http://%s:%d", serverAddress, port) + "/actuator/shutdown";
    }

    private HttpRequest usersHttpGET() {
        return HttpRequest.newBuilder()
                .uri(URI.create(usersRoute))
                .version(HTTP_2)
                .GET()
                .build();
    }

    private HttpRequest postsHttpGET() {
        return HttpRequest.newBuilder()
                .uri(URI.create(postsRoute))
                .version(HTTP_2)
                .GET()
                .build();
    }

    private HttpRequest commentsHttpGET() {
        return HttpRequest.newBuilder()
                .uri(URI.create(commentsRoute))
                .version(HTTP_2)
                .GET()
                .build();
    }

    private HttpRequest shutdownHttpPOST() {
        return HttpRequest.newBuilder()
                .uri(URI.create(shutdown))
                .version(HTTP_2)
                .POST(BodyPublishers.noBody())
                .build();
    }

    @Bean
    Map<ApiResource, HttpRequest> httpRequestMap() {
        return Map.of(
                ApiResource.USERS, usersHttpGET(),
                ApiResource.POSTS, postsHttpGET(),
                ApiResource.COMMENTS, commentsHttpGET(),
                ApiResource.SHUTDOWN, shutdownHttpPOST()
        );
    }

    @Bean
    HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    Gson gson() {
        return new Gson();
    }
}

