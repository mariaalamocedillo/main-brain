package com.mainbrain.repositories;

import com.mainbrain.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface UserRepository extends MongoRepository<User, String> {

    @Query("{username:'?0'}")
    User findUserByUsername(String username);

    @Query("{email:'?0'}")
    User findUserByEmail(String email);
}
