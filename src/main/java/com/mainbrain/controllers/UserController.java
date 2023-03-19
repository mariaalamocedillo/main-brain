package com.mainbrain.controllers;

import com.mainbrain.config.JwtTokenProvider;
import com.mainbrain.models.Role;
import com.mainbrain.models.User;
import com.mainbrain.models.UserRole;
import com.mainbrain.services.SecurityServiceImpl;
import com.mainbrain.services.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotesController.class);

    @Autowired
    private SecurityServiceImpl securityServiceImpl;
    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        //check any previous session
        if (usersService.checkAuthenticated(request) != null) {
            LOGGER.info("An user was already logged in; proceed to logout");
            securityServiceImpl.logout();
        }

        if (securityServiceImpl.login(payload.get("username"), payload.get("password"))) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String token = tokenProvider.generateToken(auth);
            LOGGER.info("User logged in; token for session created");
            return ResponseEntity.ok(token);
        }
        LOGGER.info("Couldn't log in; Username or password is incorrect");
        return ResponseEntity.badRequest().body("Username or password is incorrect");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        User _user = User.builder()
                .username(payload.get("username"))
                .email(payload.get("email"))
                .password(payload.get("password"))
                .userRoles(Collections.singleton(new UserRole(new Role("ROLE_USER"))))
                .notesIds(new ArrayList<>())
                .build();

        String checkUser = usersService.checkIfUserExists(payload.get("email"), payload.get("username"));
        if (Objects.equals(checkUser, "username")) {
            return ResponseEntity.badRequest().body("Username already in use");
        } else if(Objects.equals(checkUser, "email")) {
            return ResponseEntity.badRequest().body("This email already has an account");
        }

        boolean registrado = securityServiceImpl.register(_user);
        if (registrado) {
            // Si el registro es exitoso, iniciamos sesión y devolvemos el token de login
            securityServiceImpl.login(_user.getUsername(), _user.getPassword());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("User has been registered and a token has been created");
            return ResponseEntity.ok(tokenProvider.generateToken(auth));
        } else {
            // Si el registro falla, devolvemos un mensaje de error
            LOGGER.info("User already exists");
            return ResponseEntity.badRequest().body("User already exists");
        }
    }

    @PostMapping("/myNotes")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        // check if the token is valid
        User user = usersService.checkAuthenticated(request);
        if (user != null) {
            LOGGER.info("Valid session and user");
            return ResponseEntity.ok(user.getNotesIds());
        } else {
            LOGGER.info("The session is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkUser(HttpServletRequest request) {
        // check if the token is valid
        if(usersService.checkAuthenticated(request) == null) {
            return ResponseEntity.ok("no-autorizado");
        }
        return ResponseEntity.ok("autorizado");
    }

    @PostMapping("/getAllUsers")
    public ResponseEntity<?> gatAllUsers(HttpServletRequest request) {
        User author = usersService.checkAuthenticated(request);
        if(author == null) {
            return ResponseEntity.ok("no-autorizado");
        }
        return ResponseEntity.ok(usersService.findAllUsers(author));
    }

}