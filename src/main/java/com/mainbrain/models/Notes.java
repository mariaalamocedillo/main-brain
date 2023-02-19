package com.mainbrain.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mainbrain.config.ObjectIdSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notes")
@Data
@AllArgsConstructor
@NoArgsConstructor
//@CompoundIndex(def = "{'name': 1, 'author': 1}", unique = true)
public class Notes {
    @Id
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    private String name;
    private String tasks;
    private String author;

    public Notes(String name, String tasks, String author) {
        this.name = name;
        this.tasks = tasks;
        this.author = author;
    }
}
