package org.example.dictionarybot.handler.outcome.telegram.impl;

import lombok.RequiredArgsConstructor;
import org.example.dictionarybot.dto.response.voice.VoiceMessageDto;
import org.example.dictionarybot.dto.response.webapp.WebAppLinkDto;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramWebAppLinkHandler implements TelegramMessageHandler<WebAppLinkDto> {
    @Override
    public void handle(DefaultAbsSender bot, WebAppLinkDto message) throws TelegramApiException {
        SendMessage sendMessage = getBasicSendMessage(message);
        sendMessage.setText(message.getTitle());
        sendMessage.setParseMode("HTML");

        if (message.getWeb_app() != null && message.getWeb_app().getUrl() != null) {
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("Открыть WebApp");
            button.setWebApp(new WebAppInfo(message.getWeb_app().getUrl()));
            markup.setKeyboard(List.of(List.of(button)));
            sendMessage.setReplyMarkup(markup);
        }

        bot.execute(sendMessage);
    }

    @Override
    public Class<WebAppLinkDto> getGenericParameter() {
        return WebAppLinkDto.class;
    }
}
