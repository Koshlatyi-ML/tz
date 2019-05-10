package app.convert;

import java.util.Map;

import app.entity.Comment;
import app.entity.Post;
import com.google.gson.JsonDeserializer;

@FunctionalInterface
public interface CommentParser {
    JsonDeserializer<Comment> getDeserializer(Map<Long, Post> postMap);
}
