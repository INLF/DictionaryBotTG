package org.example.dictionarybot.dto.response.menu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.data.MenuRowDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

import static org.example.dictionarybot.dto.response.MessageType.MENU_MESSAGE;


@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class MessageMenuDto extends AbstractMessageDto {
    private final String text;
    private final List<MenuRowDto> rows;

    @JsonCreator
    public MessageMenuDto(@JsonProperty("chatId") String chatId,
                          @JsonProperty("text") String text,
                          @JsonProperty("rows") List<MenuRowDto> rows,
                          @JsonProperty("telegramMessageMetadata") TelegramMessageMetadata telegramMessageMetadata) {
        super(chatId, MENU_MESSAGE, telegramMessageMetadata);
        this.text = text;
        this.rows = rows;
    }
}
