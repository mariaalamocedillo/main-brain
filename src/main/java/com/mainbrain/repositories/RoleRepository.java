package com.mainbrain.repositories;
import java.util.Optional;

import com.mainbrain.models.ERole;
import com.mainbrain.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
