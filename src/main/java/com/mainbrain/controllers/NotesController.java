package com.mainbrain.controllers;

import com.mainbrain.models.Notes;
import com.mainbrain.services.NotesService;
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

    @GetMapping
    public ResponseEntity<List<Notes>> root(){
        return new ResponseEntity<>(notesService.allNotes(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Notes>> getNoteById(@PathVariable ObjectId id){
        return new ResponseEntity<>(notesService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Optional<Notes>> getNoteByName(@PathVariable String name){
        return new ResponseEntity<>(notesService.findByName(name), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Notes> createNote(@RequestBody Map<String, String> payload){
        return new ResponseEntity<>(notesService.createNotes(payload.get("name").trim(),
                payload.get("tasks"), payload.get("author")), HttpStatus.CREATED);
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
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
