package org.example.dictionarybot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Properties;

@Service
@Slf4j
public class PropertyService {
    public String getMessageProperty(String name) {
        return loadProperties().getProperty(name);
    }

    private Properties loadProperties() {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("message.properties");
        Properties properties = new Properties();

        try {
            properties.load(inputStream);
        } catch (Exception e) {
            log.info("Can't load {} file", "message.properties");
        }

        return properties;
    }
}

