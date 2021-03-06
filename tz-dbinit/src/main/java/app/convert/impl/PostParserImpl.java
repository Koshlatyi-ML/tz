package app.convert.impl;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.convert.PostParser;
import app.entity.Post;
import app.entity.User;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

@Component
public class PostParserImpl implements PostParser {

    private static final String USER_ID = "userId";

    private Gson gson;

    @Override
    public JsonDeserializer<Post>getDeserializer(Map<Long, User> userMap) {
        return (json, type, context) -> {
            var post = gson.fromJson(json.getAsJsonObject(), Post.class);
            setUpRelations(userMap).accept(post, json);
            return post;
        };
    }

    private BiConsumer<Post, JsonElement> setUpRelations(Map<Long, User> map) {
        return (post, json) -> {
            var postIdJsonElem = json.getAsJsonObject().get(USER_ID);
            Optional.ofNullable(postIdJsonElem).ifPresent(jsonElement -> {
                var postId = jsonElement.getAsLong();
                Optional.ofNullable(map.get(postId)).ifPresent(addPostToUser(post));
            });
        };
    }

    private Consumer<User> addPostToUser(Post post) {
        return user -> Optional.ofNullable(user.getPosts()).ifPresentOrElse(
                p -> p.add(post),
                () -> initPosts(user, post)
        );
    }

    private void initPosts(User user, Post post) {
        user.setPosts(new ArrayList<>());
        user.getPosts().add(post);
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }
}
