package app.convert;

import java.util.Map;

import app.entity.Post;
import app.entity.User;
import com.google.gson.JsonDeserializer;

@FunctionalInterface
public interface PostParser  {
    JsonDeserializer<Post> getDeserializer(Map<Long, User> userMap);
}
