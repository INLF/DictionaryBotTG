package org.example.dictionarybot.dto.response.sticker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static org.example.dictionarybot.dto.response.MessageType.STICKER_MESSAGE;


@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class StickerMessageDto extends AbstractMessageDto {
    private final String fileId;

    @JsonCreator
    public StickerMessageDto(@JsonProperty("chatId") String chatId,
                                @JsonProperty("telegramMessageMetadata") TelegramMessageMetadata telegramMessageMetadata,
                                @JsonProperty("fileId") String fileId) {
        super(chatId, STICKER_MESSAGE, telegramMessageMetadata);
        this.fileId = fileId;
    }
}
