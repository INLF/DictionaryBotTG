package org.example.dictionarybot.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static org.example.dictionarybot.dto.request.UserMessageType.AUDIO;


@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class AudioUserMessageDto extends AbstractUserMessageDto {
    private final String fileId;
    private final String caption;

    @JsonCreator
    public AudioUserMessageDto(@JsonProperty("messageId") String messageId,
                               @JsonProperty("chatId") String chatId,
                               @JsonProperty("timeStamp") Long timeStamp,
                               @JsonProperty("fileId") String fileId,
                               @JsonProperty("userId") String userId,
                               @JsonProperty("caption") String caption) {
        super(chatId, AUDIO, userId, messageId, timeStamp);
        this.fileId = fileId;
        this.caption = caption;
    }
}