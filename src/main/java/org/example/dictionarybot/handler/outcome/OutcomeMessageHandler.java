package org.example.dictionarybot.handler.outcome;

import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@Slf4j
public class OutcomeMessageHandler {
    private final List<TelegramMessageHandler<? extends AbstractMessageDto>> telegramMessageHandlers;
    private final DefaultAbsSender telegramBot;

    public OutcomeMessageHandler(List<TelegramMessageHandler<? extends AbstractMessageDto>> telegramMessageHandlers,
                                 DefaultAbsSender telegramBot) {
        this.telegramMessageHandlers = telegramMessageHandlers;
        this.telegramBot = telegramBot;
    }

    public void handle(AbstractMessageDto messageDto) {
        try {
            send(messageDto);
        } catch (Exception e) {
            log.error("Error handle outcome message", e);
        }

    }

    private void send(AbstractMessageDto messageDto) throws TelegramApiException {
        TelegramMessageHandler<? extends AbstractMessageDto> handler = telegramMessageHandlers
                .stream()
                .filter(h -> h.getGenericParameter().equals(messageDto.getClass()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown MessageEntity: " + messageDto.getClass().getSimpleName()));
        handleSafely(handler, messageDto);
    }

    private <T extends AbstractMessageDto> void handleSafely(TelegramMessageHandler<T> handler, AbstractMessageDto messageDto) throws TelegramApiException {
        handler.handle(telegramBot, (T) messageDto);
    }
}
