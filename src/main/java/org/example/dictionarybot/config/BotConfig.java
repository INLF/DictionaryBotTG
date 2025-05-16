package org.example.dictionarybot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
public class BotConfig {

    @Value("${telegram.bot.token}")
    private String token;
    @Bean
    public DefaultAbsSender telegramBot() {
        return new DefaultAbsSender(new DefaultBotOptions(), token) {};
    }
}

