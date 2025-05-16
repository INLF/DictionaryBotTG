package org.example.dictionarybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class DictionaryBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DictionaryBotApplication.class, args);
    }

}
