package app.repository;

import org.springframework.data.repository.CrudRepository;

import app.entity.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {
}
