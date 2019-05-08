package app.convert.impl;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.convert.CommentParser;
import app.entity.Comment;
import app.entity.Post;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

@Component
public class CommentParserImpl implements CommentParser {

    private static final String POST_ID = "postId";

    private Gson gson;

    @Override
    public Function<Map<Long, Post>, JsonDeserializer<Comment>> getDeserializer() {
        return map -> (JsonDeserializer<Comment>) (json, type, context) -> {
            var comment = gson.fromJson(json.getAsJsonObject(), Comment.class);
            setUpRelations(map).accept(comment, json);
            return comment;
        };
    }

    private BiConsumer<Comment, JsonElement> setUpRelations(Map<Long, Post> map) {
        return (comment, json) -> {
            var postIdJsonElem = json.getAsJsonObject().get(POST_ID);
            Optional.ofNullable(postIdJsonElem).ifPresent(jsonElement -> {
                var postId = jsonElement.getAsLong();
                Optional.ofNullable(map.get(postId)).ifPresent(addCommentToPost(comment));
            });
        };
    }

    private Consumer<Post> addCommentToPost(Comment comment) {
        return post -> Optional.ofNullable(post.getComments()).ifPresentOrElse(
                p -> p.add(comment),
                () -> {
                    post.setComments(new ArrayList<>());
                    post.getComments().add(comment);
                }
        );
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }
}
