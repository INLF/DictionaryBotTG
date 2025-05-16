package org.example.dictionarybot.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.ToString;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlainTextUserMessageDto.class, name = "PLAIN_TEXT"),
        @JsonSubTypes.Type(value = CallBackQueryUserMessageDto.class, name = "CALLBACK_QUERY"),
        @JsonSubTypes.Type(value = CommandUserMessageDto.class, name = "COMMAND"),
        @JsonSubTypes.Type(value = StickerUserMessageDto.class, name = "STICKER"),
        @JsonSubTypes.Type(value = PhotoUserMessageDto.class, name = "PHOTO"),
        @JsonSubTypes.Type(value = VoiceUserMessageDto.class, name = "VOICE"),
        @JsonSubTypes.Type(value = AudioUserMessageDto.class, name = "AUDIO"),
        @JsonSubTypes.Type(value = VideoUserMessageDto.class, name = "VIDEO"),
        @JsonSubTypes.Type(value = VideoNoteUserMessageDto.class, name = "VIDEO_NOTE")
})
@ToString
public abstract class AbstractUserMessageDto {
    private final String chatId;
    private final UserMessageType messageType;
    private final String userId;
    private final String messageId;
    private final Long timeStamp;

    protected AbstractUserMessageDto(String chatId, UserMessageType messageType, String userId, String messageId, Long timeStamp) {
        this.chatId = chatId;
        this.messageType = messageType;
        this.userId = userId;
        this.messageId = messageId;
        this.timeStamp = timeStamp;
    }
}
