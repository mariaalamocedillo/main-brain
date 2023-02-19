package com.mainbrain.repositories;

import com.mainbrain.models.Notes;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotesRepository extends MongoRepository<Notes, ObjectId> {

    Optional<Notes> findByName(String name);

    void deleteById(ObjectId id);

    Notes findById(String id);
}
