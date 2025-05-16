package org.example.dictionarybot.dispatcher;

import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.client.BotMessageClient;
import org.example.dictionarybot.dto.request.*;
import org.example.dictionarybot.exceptions.MessageDispatcherException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.example.dictionarybot.service.PropertyService;

import java.time.Instant;
import java.util.List;
import java.util.Objects;


// Could've decouple logic of parsing Update and dispatching it
@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramMessageDispatcher implements MessageDispatcher<Update> {
    private final PropertyService propertyService;
    private final BotMessageClient botMessageClient;


    @Override
    public void dispatch(Update update) throws MessageDispatcherException {
        if (!update.hasCallbackQuery()) {
            Message message = update.getMessage();
            if (message == null) {
                return;
            }
            String text = message.getText();
            // TODO add sticker handling
            //Sticker sticker = message.getSticker();
            List<PhotoSize> photos = message.getPhoto();
            String caption = message.getCaption();
            String userId = (message.getFrom() != null) ? String.valueOf(message.getFrom().getId()) : null;

            //photo
            if (photos != null && !photos.isEmpty()) {
                botMessageClient.sendIncomeMessage(
                        new PhotoUserMessageDto(
                                String.valueOf(message.getMessageId()),
                                String.valueOf(message.getChatId()),
                                message.getDate().longValue(),
                                photos.getLast().getFileId(),
                                userId,
                                caption
                        )
                );
                return;
            }

            //voice
            Voice voice = message.getVoice();
            if (voice != null && voice.getFileId() != null) {
                if (voice.getDuration() != null && voice.getDuration() > 60) {
                    throw new MessageDispatcherException(getBotLimitDurationMessage(message.getChatId()));
                }
                botMessageClient.sendIncomeMessage(
                        new VoiceUserMessageDto(
                                String.valueOf(message.getMessageId()),
                                String.valueOf(message.getChatId()),
                                message.getDate().longValue(),
                                voice.getFileId(),
                                userId,
                                caption)
                );
                return;
            }

            //audio
            Audio audio = message.getAudio();
            if (audio != null && audio.getFileId() != null) {
                if (audio.getDuration() != null && audio.getDuration() > 60) {
                    throw new MessageDispatcherException(getBotLimitDurationMessage(message.getChatId()));
                }
                botMessageClient.sendIncomeMessage(
                        new AudioUserMessageDto(
                                String.valueOf(message.getMessageId()),
                                String.valueOf(message.getChatId()),
                                message.getDate().longValue(),
                                audio.getFileId(),
                                userId,
                                caption
                        )
                );
                return ;
            }

            //video
            Video video = message.getVideo();
            if (video != null && video.getFileId() != null) {
                if (video.getDuration() != null && video.getDuration() > 60) {
                    throw new MessageDispatcherException(getBotLimitDurationMessage(message.getChatId()));
                }
                botMessageClient.sendIncomeMessage(
                        new VideoUserMessageDto(
                                String.valueOf(message.getMessageId()),
                                String.valueOf(message.getChatId()),
                                message.getDate().longValue(),
                                video.getFileId(),
                                userId,
                                caption
                        )
                );
                return ;
            }

            //video note
            VideoNote videoNote = message.getVideoNote();
            if (videoNote != null && videoNote.getFileId() != null) {
                if (videoNote.getDuration() != null && videoNote.getDuration() > 60) {
                    throw new MessageDispatcherException(getBotLimitDurationMessage(message.getChatId()));
                }
                botMessageClient.sendIncomeMessage(
                        new VideoNoteUserMessageDto(
                                String.valueOf(message.getMessageId()),
                                String.valueOf(message.getChatId()),
                                message.getDate().longValue(),
                                videoNote.getFileId(),
                                userId,
                                caption
                        )
                );
                return ;
            }

            if (message.isCommand()) {
                String command = update.getMessage().getText();
                log.info("Got command: {} ", command);
                botMessageClient.sendIncomeMessage(
                        new CommandUserMessageDto(
                                String.valueOf(message.getMessageId()),
                                String.valueOf(message.getChatId()),
                                message.getDate().longValue(),
                                command,
                                userId
                        )
                );
                return;
            }

            if (text == null) {
                throw new MessageDispatcherException(SendMessage
                        .builder()
                        .chatId(message.getChatId())
                        .parseMode("HTML")
                        .text(propertyService.getMessageProperty("unexpected_telegram_message_type"))
                        .build());
            }
            PlainTextUserMessageDto defaultUserMessageDto = convertTelegramMessageToUserMessageDto(message);
            botMessageClient.sendIncomeMessage(defaultUserMessageDto);
        } else {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            log.info("Got callback: {} ", callbackQuery.getData());
            log.info("Got callback full: {} ", callbackQuery);
            botMessageClient.sendIncomeMessage(convertCallbackQueryToUserDto(update));
        }
    }

    public SendMessage getBotLimitDurationMessage(Long chatId) {
        return SendMessage
                .builder()
                .chatId(chatId)
                .parseMode("HTML")
                .text(propertyService.getMessageProperty("limit_duration_message"))
                .build();
    }

    // TODO: add null constraints
    private CallBackQueryUserMessageDto convertCallbackQueryToUserDto(Update update) {
        return new CallBackQueryUserMessageDto(
                String.valueOf(update.getCallbackQuery().getMessage().getMessageId()),
                String.valueOf(update.getCallbackQuery().getMessage().getChatId()),
                Instant.now().getEpochSecond(),
                update.getCallbackQuery().getData(),
                String.valueOf(update.getCallbackQuery().getFrom().getId())
        );
    }

    @SneakyThrows
    private PlainTextUserMessageDto convertTelegramMessageToUserMessageDto(Message message) {
        String userId = (message.getFrom() != null) ? String.valueOf(message.getFrom().getId()) : null;
        return new PlainTextUserMessageDto(
                String.valueOf(message.getMessageId()),
                String.valueOf(message.getChatId()),
                message.getDate().longValue(),
                message.getText(),
                userId
        );
    }
}
