package com.mainbrain.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mainbrain.config.JwtTokenProvider;
import com.mainbrain.models.AuthorizationDeserializer;
import com.mainbrain.models.Role;
import com.mainbrain.models.User;
import com.mainbrain.models.UserRole;
import com.mainbrain.services.SecurityServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpServletRequest request) {

        boolean result = securityServiceImpl.login(payload.get("username"), payload.get("password"));
        if (result) {
            //Creation of a session
            HttpSession session = request.getSession();
            session.setAttribute("userToken", SecurityContextHolder.getContext().getAuthentication().toString());
            //session.setAttribute("userToken", tokenProvider.generateToken(SecurityContextHolder.getContext().getAuthentication()));

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


            /*Map<String, Object> response = new HashMap<>();
            response.put("authentication", SecurityContextHolder.getContext().getAuthentication());
            response.put("user", user);*/

            return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication());
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
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        System.out.println("RESOLVER"+tokenProvider.resolveToken(request));
        String userToken = request.getHeader("Authorization");
        if (userToken != null && !userToken.isEmpty()) {
            // Aquí puedes validar el token y obtener la información del usuario si es válido
            //User userDetails = tokenProvider.getUserPrincipalFromToken(userToken.replaceAll("Bearer ", ""));
            String datos = userToken.replaceAll("Bearer ", "");


            return ResponseEntity.ok("userDetails estan bn" );
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


}
