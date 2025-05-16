package org.example.dictionarybot.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static org.example.dictionarybot.dto.request.UserMessageType.PHOTO;


@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class PhotoUserMessageDto extends AbstractUserMessageDto {
    private final String fileId;
    private final String caption;

    @JsonCreator
    public PhotoUserMessageDto(@JsonProperty("messageId") String messageId,
                               @JsonProperty("chatId") String chatId,
                               @JsonProperty("timeStamp") Long timeStamp,
                               @JsonProperty("fileId") String fileId,
                               @JsonProperty("userId") String userId,
                               @JsonProperty("caption") String caption) {
        super(chatId, PHOTO, userId, messageId, timeStamp);
        this.fileId = fileId;
        this.caption = caption;
    }
}