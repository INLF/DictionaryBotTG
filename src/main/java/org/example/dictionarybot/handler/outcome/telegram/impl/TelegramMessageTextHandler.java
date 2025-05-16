package org.example.dictionarybot.handler.outcome.telegram.impl;


import org.example.dictionarybot.dto.response.text.MessageTextDto;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramMessageTextHandler implements TelegramMessageHandler<MessageTextDto> {
    @Override
    public void handle(DefaultAbsSender bot, MessageTextDto message) throws TelegramApiException {
        SendMessage sendMessage = getBasicSendMessage(message);
        sendMessage.setText(message.getText());
        sendMessage.setParseMode("HTML");
        bot.execute(sendMessage);
    }

    @Override
    public Class<MessageTextDto> getGenericParameter() {
        return MessageTextDto.class;
    }
}
