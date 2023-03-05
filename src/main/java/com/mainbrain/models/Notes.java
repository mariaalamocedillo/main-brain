package com.mainbrain.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mainbrain.config.ObjectIdSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notes {
    @Id
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    private String name;
    private String tasks;
    private String author;  //who created the note
    private String holder;  //the one with the note in its board
    private String colour;

    public Notes(String name, String tasks, String author, String holder) {
        this.name = name;
        this.tasks = tasks;
        this.author = author;
        this.holder = holder;
    }
}
