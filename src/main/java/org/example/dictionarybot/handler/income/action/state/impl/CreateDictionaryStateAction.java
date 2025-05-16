package org.example.dictionarybot.handler.income.action.state.impl;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.request.PlainTextUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.MessageMenuDto;
import org.example.dictionarybot.handler.income.action.state.StateAction;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.neo4j.service.DictionaryService;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.PropertyService;
import org.example.dictionarybot.service.message.MessageProcessingHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class CreateDictionaryStateAction implements StateAction {
    private final DictionaryService dictionaryService;
    private final RedisService redisService;
    private final MessageProcessingHelper messageProcessingHelper;
    private final PropertyService propertyService;

    @Override
    public Optional<AbstractMessageDto> process(@NonNull AbstractUserMessageDto abstractUserMessageDto,
                                                @NonNull TelegramMessageMetadata defaultMessageMetadata,
                                                @NonNull UserData userData) {
        PlainTextUserMessageDto plainTextUserMessageDto = (PlainTextUserMessageDto) abstractUserMessageDto;

        dictionaryService.addNewDictionary(
                abstractUserMessageDto.getUserId(),
                plainTextUserMessageDto.getText(),
                "ADD LATER!!!");

        return Optional.of(
                new MessageMenuDto(
                    abstractUserMessageDto.getUserId(),
                    propertyService.getMessageProperty("start_text"),
                    List.of(messageProcessingHelper.getReturnButtonRow()),
                    defaultMessageMetadata)
        );

    }

    @Override
    public UserState getUserState() {
        return UserState.CREATE_DICTIONARY;
    }
}
