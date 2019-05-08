package app.convert;

import java.util.Map;
import java.util.function.Function;

import app.entity.Comment;
import app.entity.Post;
import com.google.gson.JsonDeserializer;

@FunctionalInterface
public interface CommentParser {
    Function<Map<Long, Post>, JsonDeserializer<Comment>> getDeserializer();
}
