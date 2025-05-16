package org.example.dictionarybot.handler.outcome.telegram.impl;


import org.example.dictionarybot.dto.response.photo.PhotoMessageDto;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHandler;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramPhotoHandler implements TelegramMessageHandler<PhotoMessageDto> {
    private final TelegramMessageHelper telegramMessageHelper;

    @Override
    public void handle(DefaultAbsSender bot, PhotoMessageDto message) throws TelegramApiException {

    }

    @Override
    public Class<PhotoMessageDto> getGenericParameter() {
        return PhotoMessageDto.class;
    }
}

