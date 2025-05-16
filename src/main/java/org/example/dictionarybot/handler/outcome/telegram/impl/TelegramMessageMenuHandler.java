package org.example.dictionarybot.handler.outcome.telegram.impl;


import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.MessageMenuDto;
import org.example.dictionarybot.dto.response.menu.data.MenuRowDto;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHelper;
import org.example.dictionarybot.redis.RedisService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.time.Instant;
import java.util.List;

import static java.time.ZoneOffset.UTC;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramMessageMenuHandler implements TelegramMessageHandler<MessageMenuDto> {
    private final TelegramMessageHelper telegramMessageHelper;
    private final RedisService redisService;

    @Override
    public void handle(DefaultAbsSender bot, MessageMenuDto messageMenuDto) throws TelegramApiException {
        TelegramMessageMetadata telegramMessageMetadata = messageMenuDto.getTelegramMessageMetadata();
        if (telegramMessageMetadata.isNewMessageForEdit()) {
            Message sentMessage = sendMessage(bot, messageMenuDto);
            redisService.setEditableMessageId(String.valueOf(sentMessage.getMessageId()), messageMenuDto.getChatId());
            List<MenuRowDto> oldEditableMessageChangeMenu = telegramMessageMetadata.getOldEditableMessageChangeMenu();
            if (oldEditableMessageChangeMenu != null && !oldEditableMessageChangeMenu.isEmpty() && telegramMessageMetadata.getEditMessageId() != null) {
                EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                        .chatId(messageMenuDto.getChatId())
                        .replyMarkup(telegramMessageHelper.createInlineMenu(oldEditableMessageChangeMenu))
                        .messageId(Integer.valueOf(telegramMessageMetadata.getEditMessageId()))
                        .build();
                try {
                    bot.execute(editMessageReplyMarkup);
                } catch (TelegramApiRequestException e) {
                    if (e.getErrorCode() == 400) {
                        log.warn("Error editing reply markup for old editable message: {}", e.getApiResponse());
                    } else {
                        throw e;
                    }
                }
            }
            return;
        }
        if (telegramMessageMetadata.getEditMessageId() != null) {
            EditMessageText editMessageText = EditMessageText.builder()
                    .text(messageMenuDto.getText())
                    .chatId(messageMenuDto.getChatId())
                    .replyMarkup(telegramMessageHelper.createInlineMenu(messageMenuDto.getRows()))
                    .messageId(Integer.valueOf(telegramMessageMetadata.getEditMessageId()))
                    .parseMode("HTML")
                    .build();
            try {
                bot.execute(editMessageText);
            } catch (TelegramApiRequestException e) {
                if (e.getErrorCode() == 400) {
                    log.warn("Error editing current editable message: {}", e.getApiResponse());
                } else {
                    throw e;
                }
            }
        } else {
            sendMessage(bot, messageMenuDto);
        }
    }

    private Message sendMessage(DefaultAbsSender bot, MessageMenuDto messageMenuDto) throws TelegramApiException {
        SendMessage sendMessage = getBasicSendMessage(messageMenuDto);

        String text = messageMenuDto.getText();
        sendMessage.setText(text);

        List<MenuRowDto> menuRows = messageMenuDto.getRows();
        if (menuRows != null && !menuRows.isEmpty()) {
            sendMessage.setReplyMarkup(telegramMessageHelper.createInlineMenu(menuRows));
        } else {
            sendMessage.setReplyMarkup(ReplyKeyboardRemove.builder().removeKeyboard(true).build());
        }
        sendMessage.setParseMode("HTML");
        return bot.execute(sendMessage);
    }

    @Override
    public Class<MessageMenuDto> getGenericParameter() {
        return MessageMenuDto.class;
    }
}