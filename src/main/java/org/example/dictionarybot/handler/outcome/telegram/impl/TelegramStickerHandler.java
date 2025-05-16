package org.example.dictionarybot.handler.outcome.telegram.impl;

import org.example.dictionarybot.dto.response.sticker.StickerMessageDto;
import org.example.dictionarybot.handler.outcome.telegram.TelegramMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramStickerHandler implements TelegramMessageHandler<StickerMessageDto> {
    @Override
    public void handle(DefaultAbsSender bot, StickerMessageDto message) throws TelegramApiException {
        bot.execute(SendSticker.builder().chatId(message.getChatId()).sticker(new InputFile(message.getFileId())).build());
    }

    @Override
    public Class<StickerMessageDto> getGenericParameter() {
        return StickerMessageDto.class;
    }
}
