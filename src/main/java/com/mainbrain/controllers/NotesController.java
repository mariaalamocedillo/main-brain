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

    @Autowired
    private JwtTokenProvider tokenProvider;


    @PostMapping("/create")
    public ResponseEntity<Notes> createNote(@RequestBody Map<String, String> payload, HttpServletRequest request){
        if (tokenProvider.isTokenValid(request.getHeader("Authorization"))){
            //get the user from the token
            User userToken = tokenProvider.getUserPrincipalFromToken(tokenProvider.resolveToken(request));
            Optional<User> _user = usersService.findById(userToken.getUsername());

            if (_user.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); //user not found
            }
            User userAuthor = _user.get();
            User userHolder;
            //initialize the holder user depending on the existence of a different holder or no
            if(payload.get("sendTo") != null) {
                userHolder = usersService.findByUsername(payload.get("sendTo"));
            } else {
                userHolder = _user.get();
            }
            return new ResponseEntity<>(notesService.createNotes(payload.get("title").trim(),
                            payload.get("content").trim(), userAuthor, userHolder, payload.get("title")), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);   // user expired or non existence
    }



    @PostMapping("/update")
    public ResponseEntity<Notes> updateTutorial(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        // check if the token is valid
        if (!tokenProvider.isTokenValid(request.getHeader("Authorization"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //get the user and the note
        User userToken = tokenProvider.getUserPrincipalFromToken(tokenProvider.resolveToken(request));
        Optional<User> _user = usersService.findById(userToken.getUsername());

        ObjectId id = new ObjectId(payload.get("id").toString());
        Optional<Notes> NotesOpt = notesService.findById(id);

        //if user and note exists and username in the note matches the user's username
        if(_user.isPresent() && NotesOpt.isPresent()){
            Notes note = NotesOpt.get();
            if (Objects.equals(_user.get().getUsername(), note.getAuthor())) {
                //update the note and save it
                note.setName(payload.get("title").toString().trim());
                note.setTasks(payload.get("content").toString().trim());
                return new ResponseEntity<>(notesService.save(note), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/delete")
    public ResponseEntity<HttpStatus> deleteNote(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        // check if the token is valid
        if (!tokenProvider.isTokenValid(request.getHeader("Authorization"))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //get the user
        User userToken = tokenProvider.getUserPrincipalFromToken(tokenProvider.resolveToken(request));
        Optional<User> _user = usersService.findById(userToken.getUsername());

        if (_user.isPresent()){
            try {
                notesService.deleteById(payload.get("id").toString());
                usersService.deleteId(_user.get().getUsername(), payload.get("id").toString());
                return new ResponseEntity<>(HttpStatus.OK); //note deleted from db and user notesIds
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND); //user not found

    }
}
