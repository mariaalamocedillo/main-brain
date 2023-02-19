package com.mainbrain.repositories;

import com.mainbrain.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByUsername(String name);
    Optional<User> findByEmail(String name);
}
