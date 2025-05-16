package org.example.dictionarybot.handler.outcome.telegram.impl;

import org.example.dictionarybot.dto.response.voice.VoiceMessageDto;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHandler;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramVoiceHandler implements TelegramMessageHandler<VoiceMessageDto> {
    private final TelegramMessageHelper telegramMessageHelper;

    @Override
    public void handle(DefaultAbsSender bot, VoiceMessageDto message) throws TelegramApiException {

    }

    @Override
    public Class<VoiceMessageDto> getGenericParameter() {
        return VoiceMessageDto.class;
    }
}
