package org.example.dictionarybot.dto.response.voice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.data.MenuRowDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

import static org.example.dictionarybot.dto.response.MessageType.VOICE_MESSAGE;


@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class VoiceMessageDto extends AbstractMessageDto {
    private final String fileId;
    private final String caption;
    private final List<MenuRowDto> rows;

    @JsonCreator
    public VoiceMessageDto(@JsonProperty("chatId") String chatId,
                           @JsonProperty("telegramMessageMetadata") TelegramMessageMetadata telegramMessageMetadata,
                           @JsonProperty("fileId") String fileId,
                           @JsonProperty("caption") String caption,
                           @JsonProperty("rows") List<MenuRowDto> rows) {
        super(chatId, VOICE_MESSAGE, telegramMessageMetadata);
        this.fileId = fileId;
        this.caption = caption;
        this.rows = rows;
    }
}

