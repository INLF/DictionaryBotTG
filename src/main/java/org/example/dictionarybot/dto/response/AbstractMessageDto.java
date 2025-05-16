package org.example.dictionarybot.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.example.dictionarybot.dto.response.audio.AudioMessageDto;
import org.example.dictionarybot.dto.response.menu.MessageMenuDto;
import org.example.dictionarybot.dto.response.photo.PhotoMessageDto;
import org.example.dictionarybot.dto.response.sticker.StickerMessageDto;
import org.example.dictionarybot.dto.response.text.MessageTextDto;
import org.example.dictionarybot.dto.response.video.VideoMessageDto;
import org.example.dictionarybot.dto.response.videonote.VideoNoteMessageDto;
import org.example.dictionarybot.dto.response.voice.VoiceMessageDto;
import lombok.Getter;
import lombok.ToString;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessageTextDto.class, name = "TEXT_MESSAGE"),
        @JsonSubTypes.Type(value = MessageMenuDto.class, name = "MENU_MESSAGE"),
        @JsonSubTypes.Type(value = StickerMessageDto.class, name = "STICKER_MESSAGE"),
        @JsonSubTypes.Type(value = PhotoMessageDto.class, name = "PHOTO_MESSAGE"),
        @JsonSubTypes.Type(value = VoiceMessageDto.class, name = "VOICE_MESSAGE"),
        @JsonSubTypes.Type(value = AudioMessageDto.class, name = "AUDIO_MESSAGE"),
        @JsonSubTypes.Type(value = VideoMessageDto.class, name = "VIDEO_MESSAGE"),
        @JsonSubTypes.Type(value = VideoNoteMessageDto.class, name = "VIDEO_NOTE_MESSAGE")
})
@ToString
public abstract class AbstractMessageDto {
    private final String chatId;
    private final MessageType messageType;
    private final TelegramMessageMetadata telegramMessageMetadata;

    protected AbstractMessageDto(String chatId, MessageType messageType, TelegramMessageMetadata telegramMessageMetadata) {
        this.chatId = chatId;
        this.messageType = messageType;
        this.telegramMessageMetadata = telegramMessageMetadata;
    }
}
