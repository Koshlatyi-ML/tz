package app.convert;

import java.util.Map;
import java.util.function.Function;

import app.entity.Post;
import app.entity.User;
import com.google.gson.JsonDeserializer;

@FunctionalInterface
public interface PostParser  {
    Function<Map<Long, User>, JsonDeserializer<Post>> getDeserializer();
}
