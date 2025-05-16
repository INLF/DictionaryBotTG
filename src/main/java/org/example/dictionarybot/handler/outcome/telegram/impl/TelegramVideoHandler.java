package org.example.dictionarybot.handler.outcome.telegram.impl;

import org.example.dictionarybot.dto.response.video.VideoMessageDto;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHandler;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramVideoHandler implements TelegramMessageHandler<VideoMessageDto> {
    private final TelegramMessageHelper telegramMessageHelper;

    @Override
    public void handle(DefaultAbsSender bot, VideoMessageDto message) throws TelegramApiException {

    }

    @Override
    public Class<VideoMessageDto> getGenericParameter() {
        return VideoMessageDto.class;
    }
}
