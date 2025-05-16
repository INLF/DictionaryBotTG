package org.example.dictionarybot.handler.income.action.state.impl;

import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.handler.income.action.state.StateAction;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.redis.data.UserData;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StartStateAction implements StateAction {
    @Override
    public Optional<AbstractMessageDto> process(@NonNull AbstractUserMessageDto abstractUserMessageDto, @NonNull TelegramMessageMetadata defaultMessageMetadata, @NonNull UserData userData) {
        return Optional.empty();
    }

    @Override
    public UserState getUserState() {
        return UserState.START;
    }
}

