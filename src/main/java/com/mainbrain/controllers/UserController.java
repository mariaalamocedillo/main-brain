package com.mainbrain.controllers;

import com.mainbrain.config.JwtTokenProvider;
import com.mainbrain.models.Role;
import com.mainbrain.models.User;
import com.mainbrain.models.UserRole;
import com.mainbrain.services.SecurityServiceImpl;
import com.mainbrain.services.UsersService;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private SecurityServiceImpl securityServiceImpl;
    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        //check any previous session
        if (checkAuthenticated(request) != null) {
            System.out.println("An user was already logged in; proceed to logout");
            securityServiceImpl.logout();
        }

        if (securityServiceImpl.login(payload.get("username"), payload.get("password"))) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String token = tokenProvider.generateToken(auth);
            System.out.println(token);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.badRequest().body("Nombre de usuario o contraseña incorrectos");
        }
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
            System.out.println("SE HA REGISTRADO EL USUARIO");

            securityServiceImpl.login(_user.getUsername(), _user.getPassword());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return ResponseEntity.ok(tokenProvider.generateToken(auth));
        } else {
            // Si el registro falla, devolvemos un mensaje de error
            return ResponseEntity.badRequest().body("User already exists");
        }
    }

    @PostMapping("/myNotes")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        // check if the token is valid
        User user = checkAuthenticated(request);
        if (user != null) {
            System.out.println("Valid session and user");
            return ResponseEntity.ok(user.getNotesIds());
        } else {
            System.out.println("The session is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkUser(HttpServletRequest request) {
        // check if the token is valid
        if(checkAuthenticated(request) == null) {
            return ResponseEntity.ok("no-autorizado");
        }
        return ResponseEntity.ok("autorizado");
    }

    @PostMapping("/getAllUsers")
    public ResponseEntity<?> gatAllUsers(HttpServletRequest request) {
        User author = checkAuthenticated(request);
        if(author == null) {
            return ResponseEntity.ok("no-autorizado");
        }
        return ResponseEntity.ok(usersService.findAllUsers(author));
    }

    public User checkAuthenticated(HttpServletRequest request) {
        // check if the token is valid
        String userToken = request.getHeader("Authorization");
        if (userToken == null || userToken.isEmpty()) {
            System.out.println("There is no active session ");
            return null;
        } else if (tokenProvider.isTokenValid(userToken)) {
            // Aquí valida el token y obtiene la información del usuario si existe
            User userDetails = tokenProvider.getUserPrincipalFromToken(tokenProvider.resolveToken(request));
            Optional<User> _user = usersService.findById(userDetails.getUsername());
            if (_user.isPresent()){
                System.out.println("There is a session: User was found");
                return _user.get();
            }
            System.out.println("There is a session: User not found");
        }
        System.out.println("The session is invalid");
        return null;
    }
}