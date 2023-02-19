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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotesService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private UsersService usersService;

    public NotesService() {
    }

    public List<Notes> allNotes(){
        return notesRepository.findAll();
    }

    public Optional<Notes> findById(ObjectId id){
        return notesRepository.findById(id);
    }

    public Optional<Notes> findByName(String name){
        return notesRepository.findByName(name);
    }

    public Notes createNotes(String name, String tasks, String username){
        User user = usersService.findByUsername(username).get();
        Notes notes = notesRepository.insert(new Notes(name, tasks, username));

        mongoTemplate.update(User.class)
                .matching(Criteria.where("id").is(user.getId()))
                .apply(new Update().push("tasksListsIds").value(notes))
                .first(); //actualizar el usuario que creó la tasklist

        return notes;
    }

    public Notes save(Notes notes){
        return notesRepository.save(notes);
    }

    public void deleteById(String id){
        notesRepository.deleteById(notesRepository.findById(id).getId());
    }
}
