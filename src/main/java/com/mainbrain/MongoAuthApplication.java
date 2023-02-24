package com.mainbrain;

import com.mainbrain.config.SecurityConfig;
import com.mainbrain.config.SpringMongoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import({ SecurityConfig.class, SpringMongoConfig.class })
public class MongoAuthApplication {

    public static void main(String... args) {
        SpringApplication.run(MongoAuthApplication.class, args);
    }

}
