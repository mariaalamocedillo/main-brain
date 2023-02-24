package com.mainbrain.controllers;

import com.mainbrain.models.Notes;
import com.mainbrain.models.Role;
import com.mainbrain.models.User;
import com.mainbrain.models.UserRole;
import com.mainbrain.services.NotesService;
import com.mainbrain.services.SecurityServiceImpl;
import com.mainbrain.services.UsersService;
import io.lettuce.core.ScriptOutputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private NotesService notesService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private SecurityServiceImpl securityServiceImpl;

    @GetMapping
    public ResponseEntity<List<Notes>> root(){
        return new ResponseEntity<>(notesService.allNotes(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public String createUser(@RequestBody Map<String, String> payload){
        User user = usersService.createUser(payload.get("author"), payload.get("email"), payload.get("password"));



        return  "redirect:/home";
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        boolean result = securityServiceImpl.login(payload.get("username"), payload.get("password"));
        if (result) {
            // Si el login es exitoso, creamos una sesión y devolvemos el usuario
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ResponseEntity.ok(user);
        } else {
            // Si el login falla, devolvemos un mensaje de error
            return ResponseEntity.badRequest().body("Nombre de usuario o contraseña incorrectos");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        User _user = new User(payload.get("username"), payload.get("email"),
                payload.get("password"), Collections.singleton(new UserRole(new Role("ROLE_USER"))));

        System.out.println("INFO DEL USUARIO:");
        System.out.println("--------" + _user.getUsername() + "----" + _user.getPassword() + "-----" + _user);

        boolean registrado = securityServiceImpl.register(_user);

        if (registrado) {
            // Si el registro es exitoso, iniciamos sesión y devolvemos el usuario
            System.out.println("SE HA REGISTRADO EL USUARIO");
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ResponseEntity.ok(user);
        } else {
            // Si el registro falla, devolvemos un mensaje de error
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }
    }

/*
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody User usuario) {
        try {
            boolean registrado = securityServiceImpl.register(usuario);
            if(registrado){ //si devuelve true, se ha registrado
                return ResponseEntity.ok(usersService.findByUsername(usuario.getUsername()));
            } else {
                return ResponseEntity.badRequest().body("El usuario ya existe");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
*/


    @GetMapping("/me")
    public ResponseEntity<?> obtenerUsuarioActual() {
        // Obtenemos el usuario actual de la sesión
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(user);
    }

}
