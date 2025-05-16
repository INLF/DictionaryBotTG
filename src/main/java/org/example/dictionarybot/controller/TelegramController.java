package org.example.dictionarybot.controller;


import org.example.dictionarybot.dispatcher.MessageDispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.exceptions.MessageDispatcherException;
import org.example.dictionarybot.neo4j.repo.LexemeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/telegram")
public class TelegramController {
    private final MessageDispatcher<Update> dispatcher;


    @PostMapping
    public ResponseEntity<SendMessage> webhook(@RequestBody Update update) {
        log.info("Received update {}", update);
        try {
            dispatcher.dispatch(update);
        } catch (MessageDispatcherException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok(e.getSendMessage());
        }
        return ResponseEntity.ok().build();
    }
}
