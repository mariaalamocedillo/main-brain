package com.mainbrain.services;


import com.mainbrain.models.Notes;
import com.mainbrain.models.User;
import com.mainbrain.repositories.NotesRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotesService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private NotesRepository notesRepository;

    public NotesService() {
    }

    public Optional<Notes> findById(ObjectId id){
        return notesRepository.findById(id);
    }

    public Notes createNotes(String title, String content, User author, User holder, String colour){
        Notes notes = notesRepository.insert(new Notes(title, content, author.getUsername(), holder.getUsername()));
        if(colour != null) {
            notes.setColour(colour);
        }
        mongoTemplate.update(User.class)
                .matching(Criteria.where("id").is(holder.getId()))
                .apply(new Update().push("notesIds").value(notes))
                .first(); //actualizar el usuario que creó la nota

        return notes;
    }

    public Notes save(Notes notes){
        return notesRepository.save(notes);
    }

    public void deleteById(String id){
        notesRepository.deleteById(notesRepository.findById(id).getId());
    }
}
