package app.listener;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import app.config.ApiResource;
import app.convert.CommentParser;
import app.convert.PostParser;
import app.entity.Comment;
import app.entity.Post;
import app.entity.User;
import app.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import static java.net.http.HttpResponse.BodyHandlers;
import static java.util.Arrays.asList;

@Component
public class AppReadyListener {
    private Map<ApiResource, HttpRequest> resourceRequestMap;
    private HttpClient httpClient;
    private Gson gson;
    private CommentParser commentParser;
    private PostParser postParser;
    private UserRepository userRepository;


    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {

        var commentsJson = httpClient.sendAsync(resourceRequestMap.get(ApiResource.COMMENTS), BodyHandlers.ofString())
                .thenApplyAsync(HttpResponse::body);

        var postsJson = httpClient.sendAsync(resourceRequestMap.get(ApiResource.POSTS), BodyHandlers.ofString())
                .thenApplyAsync(HttpResponse::body);

        var usersFuture = httpClient.sendAsync(resourceRequestMap.get(ApiResource.USERS), BodyHandlers.ofString())
                .thenApplyAsync(json -> gson.fromJson(json.body(), User[].class))
                .thenApplyAsync(Arrays::asList);

        usersFuture.thenCombine(postsJson, this::populateUserRelations)
                .thenCombine(commentsJson, this::populatePostRelations)
                .thenRun(() -> usersFuture.thenAccept(userRepository::saveAll))
                .thenRunAsync(this::shutdownApp);
    }

    private List<Post> populateUserRelations(List<User> users, String postsJson) {
        var userMap = users.stream().collect(Collectors.toUnmodifiableMap(User::getId, Function.identity()));
        var deserializer = postParser.getDeserializer().apply(userMap);
        var postGson = new GsonBuilder().registerTypeAdapter(Post.class, deserializer).create();
        return asList(postGson.fromJson(postsJson, Post[].class));
    }

    private List<Comment> populatePostRelations(List<Post> posts, String commentsJson) {
        var postsMap = posts.stream().collect(Collectors.toUnmodifiableMap(Post::getId, Function.identity()));
        var deserializer = commentParser.getDeserializer().apply(postsMap);
        var commentGson = new GsonBuilder().registerTypeAdapter(Comment.class, deserializer).create();
        return asList(commentGson.fromJson(commentsJson, Comment[].class));
    }

    @SneakyThrows
    private void shutdownApp() {
        httpClient.send(resourceRequestMap.get(ApiResource.SHUTDOWN), BodyHandlers.ofString());
    }

    @Autowired
    public void setResourceRequestMap(Map<ApiResource, HttpRequest> resourceRequestMap) {
        this.resourceRequestMap = resourceRequestMap;
    }

    @Autowired
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Autowired
    public void setCommentParser(CommentParser commentParser) {
        this.commentParser = commentParser;
    }

    @Autowired
    public void setPostParser(PostParser postParser) {
        this.postParser = postParser;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}


