package com.mainbrain.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "roles")
public class Role {
    @Id
    private String id;

    private ERole name;

    public Role(ERole name) {
        this.name = name;
    }
}
