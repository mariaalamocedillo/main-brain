package com.mainbrain.repositories;

import com.mainbrain.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface RoleRepository extends MongoRepository<Role, String> {
    @Query("{name:'?0'}")
    Role findRoleByName(String name);
}
