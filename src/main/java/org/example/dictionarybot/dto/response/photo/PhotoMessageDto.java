package org.example.dictionarybot.dto.response.photo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.data.MenuRowDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

import static org.example.dictionarybot.dto.response.MessageType.PHOTO_MESSAGE;


@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class PhotoMessageDto extends AbstractMessageDto {
    private final String fileId;
    private final String caption;
    private final List<MenuRowDto> rows;


    @JsonCreator
    public PhotoMessageDto(@JsonProperty("chatId") String chatId,
                           @JsonProperty("telegramMessageMetadata") TelegramMessageMetadata telegramMessageMetadata,
                           @JsonProperty("fileId") String fileId,
                           @JsonProperty("caption") String caption,
                           @JsonProperty("rows") List<MenuRowDto> rows) {
        super(chatId, PHOTO_MESSAGE, telegramMessageMetadata);
        this.fileId = fileId;
        this.caption = caption;
        this.rows = rows;
    }
}
