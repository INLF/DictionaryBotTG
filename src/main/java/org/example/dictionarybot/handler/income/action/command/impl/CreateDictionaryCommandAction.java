package org.example.dictionarybot.handler.income.action.command.impl;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.text.MessageTextDto;
import org.example.dictionarybot.handler.income.action.command.CommandAction;
import org.example.dictionarybot.handler.income.action.command.CommonCommand;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.neo4j.service.DictionaryService;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.PropertyService;
import org.example.dictionarybot.service.message.MessageProcessingHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateDictionaryCommandAction implements CommandAction {
    private final RedisService redisService;
    private final PropertyService propertyService;

    @Override
    public Optional<AbstractMessageDto> process(@NonNull AbstractUserMessageDto abstractUserMessageDto,
                                                @NonNull String command,
                                                @NonNull TelegramMessageMetadata defaultMessageMetadata,
                                                @NonNull UserData userData) {
        userData.setUserState(UserState.CREATE_DICTIONARY);
        redisService.saveUserData(userData, abstractUserMessageDto.getUserId());

        return Optional.of(
                getHelpText(
                    abstractUserMessageDto.getChatId(),
                    defaultMessageMetadata
                )
        );
    }

    private MessageTextDto getHelpText(String chatId, TelegramMessageMetadata defaultMessageMetadata) {
        return new MessageTextDto(
                chatId,
                propertyService.getMessageProperty("dictionary_add_name_command_text"),
                defaultMessageMetadata
        );
    }

    @Override
    public CommonCommand getCommand() {
        return CommonCommand.CREATE_DICT;
    }
}
