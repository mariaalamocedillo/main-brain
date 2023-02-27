package com.mainbrain.repositories;

import com.mainbrain.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface UserRepository extends MongoRepository<User, String> {

    User findByEmailOrUsername(String username, String email);
    User findByUsername(String username);
    User findByEmail(String email);
}
