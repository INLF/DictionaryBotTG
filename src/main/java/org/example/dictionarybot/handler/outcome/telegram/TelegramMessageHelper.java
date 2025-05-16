package org.example.dictionarybot.handler.outcome.telegram;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.dictionarybot.dto.response.menu.data.MenuRowDto;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;


import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramMessageHelper {
    public InlineKeyboardMarkup createInlineMenu(@NonNull List<MenuRowDto> rows) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = rows.stream()
                .map(row -> row.getItems().stream()
                        .map(item -> getButton(item.getTitle(), item.getCommand(), item.getUrl()))
                        .toList())
                .toList();
        keyboardMarkup.setKeyboard(rowsInline);
        return keyboardMarkup;
    }

    private InlineKeyboardButton getButton(@NonNull String text, String callbackData, String url) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        button.setUrl(url);
        return button;
    }

}
