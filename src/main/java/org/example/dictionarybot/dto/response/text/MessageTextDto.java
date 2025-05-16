package org.example.dictionarybot.dto.response.text;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static org.example.dictionarybot.dto.response.MessageType.TEXT_MESSAGE;


@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class MessageTextDto extends AbstractMessageDto {
    private final String text;

    @JsonCreator
    public MessageTextDto(
            @JsonProperty("chatId") String chatId,
            @JsonProperty("text") String text,
            @JsonProperty("telegramMessageMetadata") TelegramMessageMetadata telegramMessageMetadata) {
        super(chatId, TEXT_MESSAGE, telegramMessageMetadata);
        this.text = text;
    }
}
