package org.example.dictionarybot.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static org.example.dictionarybot.dto.request.UserMessageType.STICKER;


@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class StickerUserMessageDto extends AbstractUserMessageDto {
    private final String fileId;

    @JsonCreator
    public StickerUserMessageDto(@JsonProperty("messageId") String messageId,
                                 @JsonProperty("chatId") String chatId,
                                 @JsonProperty("timeStamp") Long timeStamp,
                                 @JsonProperty("fileId") String fileId,
                                 @JsonProperty("userId") String userId) {
        super(chatId, STICKER, userId, messageId, timeStamp);
        this.fileId = fileId;
    }
}
