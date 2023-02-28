package com.mainbrain.controllers;

import com.mainbrain.models.Role;
import com.mainbrain.models.User;
import com.mainbrain.models.UserRole;
import com.mainbrain.services.SecurityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private SecurityServiceImpl securityServiceImpl;


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> payload) {

        System.out.println("PROCEDEMOS AL LOGIN -->" + securityServiceImpl.login(payload.get("username"), payload.get("password")));

        boolean result = securityServiceImpl.login(payload.get("username"), payload.get("password"));
        if (result) {
            // Si el login es exitoso, creamos una sesión y devolvemos el usuario
            // Obtain the user details from the Spring Security context
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Create a new instance of your User class using the user details
            User user = User.builder()
                    .username(userDetails.getUsername())
                    .password(userDetails.getPassword())
                    .userRoles((Set<UserRole>) userDetails.getAuthorities())
                    .build();

            System.out.println("USER EN EL LOGIN DEL CONTROLER " +user);
            return ResponseEntity.ok(user);
        } else {
            // Si el login falla, devolvemos un mensaje de error
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

        System.out.println("INFO DEL USUARIO:");
        System.out.println("--------" + _user);

        boolean registrado = securityServiceImpl.register(_user);

        if (registrado) {
            // Si el registro es exitoso, iniciamos sesión y devolvemos el usuario
            System.out.println("SE HA REGISTRADO EL USUARIO");
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            System.out.println(user);
            return ResponseEntity.ok(user);
        } else {
            // Si el registro falla, devolvemos un mensaje de error
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }
    }

    @PostMapping("/me")
    public ResponseEntity<?> obtenerUsuarioActual() {
        // Obtenemos el usuario actual de la sesión
        //User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //return ResponseEntity.ok(user);

        SecurityContextHolder.getContext().setAuthentication(null);
        System.out.println("A LOGEAR: ");
        //System.out.println("PROCEDEMOS AL LOGIN -->" + securityServiceImpl.login("yo", "maria"));

        //System.out.println(SecurityContextHolder.setContext().getAuthentication());



        System.out.println(SecurityContextHolder.getContext().getAuthentication());



        return ResponseEntity.ok().body("El usuario esta en alguna parte");
    }


}
