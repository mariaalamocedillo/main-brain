package com.mainbrain.controllers;

import com.mainbrain.config.JwtTokenProvider;
import com.mainbrain.config.SecurityConfig;
import com.mainbrain.models.Notes;
import com.mainbrain.models.User;
import com.mainbrain.services.NotesService;
import com.mainbrain.services.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    @Autowired
    private UsersService usersService;


    @PostMapping("/create")
    public ResponseEntity<Notes> createNote(@RequestBody Map<String, String> payload, HttpServletRequest request){
        // check if the user is valid
        User userAuthor = usersService.checkAuthenticated(request);
        if (userAuthor == null) {
            System.out.println("The session is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User userHolder;
        //initialize the holder user depending on the existence of a different holder or no
        if(payload.get("sendTo") != null) {
            userHolder = usersService.findByUsername(payload.get("sendTo"));
        } else {
            userHolder = userAuthor;
        }
        return new ResponseEntity<>(notesService.createNotes(payload.get("title").trim(),
                payload.get("content").trim(), userAuthor, userHolder, payload.get("colour")), HttpStatus.CREATED);
    }



    @PostMapping("/update")
    public ResponseEntity<Notes> updateTutorial(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        // check if the user is valid
        User user = usersService.checkAuthenticated(request);
        if (user == null) {
            System.out.println("The session is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // get the note optional
        ObjectId id = new ObjectId(payload.get("id").toString());
        Optional<Notes> notesOpt = notesService.findById(id);

        // if user and note exists and username in the note matches the user's username
        if(notesOpt.isPresent() && user.getUsername().equals(notesOpt.get().getAuthor())) {
            Notes note = notesOpt.get();
            note.setName(payload.get("title").toString().trim());
            note.setTasks(payload.get("content").toString().trim());
            return new ResponseEntity<>(notesService.save(note), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteNote(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        // check if the token is valid
        User user = usersService.checkAuthenticated(request);
        if (user == null) {
            System.out.println("The session is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // proceed to delete the note or send an error response
        try {
            notesService.deleteById(payload.get("id").toString());
            usersService.deleteId(user.getUsername(), payload.get("id").toString());
            return new ResponseEntity<>(HttpStatus.OK); //note deleted from db and user notesIds
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
