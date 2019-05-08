package app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.entity.Post;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
}
