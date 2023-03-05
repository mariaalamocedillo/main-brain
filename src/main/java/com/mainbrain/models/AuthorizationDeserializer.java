package com.mainbrain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationDeserializer {

    private String name;

    private String credentials;

    @JsonProperty("authorities")
    private List<Authority> authoritiesList;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    private com.mainbrain.models.User principal;

    @Data
    public static class User {

        private String id;

        private String username;

        private String email;

        private String password;

        private List<UserRole> userRoles;

        private List<Note> notesIds;

        public User() {
            // Constructor por defecto sin argumentos
        }

    }

    @Data
    public static class UserRole {

        private Role role;

        private String authority;

        @Data
        public static class Role {

            private String id;

            private String name;
        }
    }

    @Data
    public static class Note {

        private String id;

        private String name;

        private String tasks;

        private String author;
    }

    @Data
    public static class Authority {

        private String authority;
    }
}
