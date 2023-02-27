package com.mainbrain.controllers;

import com.mainbrain.models.Notes;
import com.mainbrain.models.User;
import com.mainbrain.services.NotesService;
import com.mainbrain.services.UsersService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    @Autowired
    private UsersService usersService;

    @GetMapping
    public ResponseEntity<List<Notes>> root(){
        return new ResponseEntity<>(notesService.allNotes(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Notes> createNote(@RequestBody Map<String, String> payload){
        User user = usersService.findByUsername("yo"); //TODO cambio a usuario del payload
        return new ResponseEntity<>(notesService.createNotes(payload.get("name").trim(),
                payload.get("tasks"), user), HttpStatus.CREATED);
    }



    @PostMapping("/update")
    public ResponseEntity<Notes> updateTutorial(@RequestBody Map<String, Object> payload) {
        ObjectId id = new ObjectId(payload.get("id").toString());
        Optional<Notes> NotesOpt = notesService.findById(id);

        if (NotesOpt.isPresent()) {
            Notes _notes = NotesOpt.get();
            _notes.setName(payload.get("name").toString());
            _notes.setTasks(payload.get("tasks").toString());
            return new ResponseEntity<>(notesService.save(_notes), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<HttpStatus> deleteNote(@RequestBody Map<String, Object> payload) {
        try {
            notesService.deleteById(payload.get("id").toString());
            usersService.deleteId(payload.get("username").toString(), payload.get("id").toString());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
