package org.example.dictionarybot.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static org.example.dictionarybot.dto.request.UserMessageType.VIDEO;


@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class VideoUserMessageDto extends AbstractUserMessageDto {
    private final String fileId;
    private final String caption;

    @JsonCreator
    public VideoUserMessageDto(@JsonProperty("messageId") String messageId,
                               @JsonProperty("chatId") String chatId,
                               @JsonProperty("timeStamp") Long timeStamp,
                               @JsonProperty("fileId") String fileId,
                               @JsonProperty("userId") String userId,
                               @JsonProperty("caption") String caption) {
        super(chatId, VIDEO, userId, messageId, timeStamp);
        this.fileId = fileId;
        this.caption = caption;
    }
}
