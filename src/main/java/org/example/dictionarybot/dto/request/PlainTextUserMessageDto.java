package org.example.dictionarybot.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public final class PlainTextUserMessageDto extends AbstractUserMessageDto {
    private final String text;

    @JsonCreator
    public PlainTextUserMessageDto(@JsonProperty("messageId") String messageId,
                                 @JsonProperty("chatId") String chatId,
                                 @JsonProperty("timeStamp") Long timeStamp,
                                 @JsonProperty("text") String text,
                                 @JsonProperty("userId") String userId) {
        super(chatId, UserMessageType.PLAIN_TEXT, userId, messageId, timeStamp);
        this.text = text;
    }
}
