package app.repository;

import org.springframework.data.repository.CrudRepository;

import app.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
