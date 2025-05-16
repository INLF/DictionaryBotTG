package org.example.dictionarybot.handler.income.action.state;

import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.redis.data.UserData;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface StateAction {
    Optional<AbstractMessageDto> process(@NonNull AbstractUserMessageDto abstractUserMessageDto, @NonNull TelegramMessageMetadata defaultMessageMetadata, @NonNull UserData userData);

    UserState getUserState();
}
