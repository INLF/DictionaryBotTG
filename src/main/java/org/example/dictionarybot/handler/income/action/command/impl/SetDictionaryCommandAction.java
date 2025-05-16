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
import org.example.dictionarybot.neo4j.data.Dictionary;
import org.example.dictionarybot.neo4j.service.DictionaryService;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.PropertyService;
import org.example.dictionarybot.service.message.MessageProcessingHelper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class SetDictionaryCommandAction implements CommandAction {
    private final RedisService redisService;
    private final DictionaryService dictionaryService;
    private final PropertyService propertyService;
    private final MessageProcessingHelper messageProcessingHelper;

    @Override
    public Optional<AbstractMessageDto> process(@NonNull AbstractUserMessageDto abstractUserMessageDto,
                                                @NonNull String command,
                                                @NonNull TelegramMessageMetadata defaultMessageMetadata,
                                                @NonNull UserData userData) {
        Map<String, String> params =  messageProcessingHelper.getCommandParams(command);
        String dictId = params.get(MessageProcessingHelper.DICT_ID_PARAM);

        Dictionary dictionary = dictionaryService.getDictionaryById(dictId);

        userData.setChosenDictionary(dictionary);

        redisService.saveUserData(userData, abstractUserMessageDto.getUserId());
        return Optional.of(
                new MessageTextDto(
                        abstractUserMessageDto.getChatId(),
                        String.format(propertyService.getMessageProperty("current_dictionary_successfully_set"), dictionary.name),
                        defaultMessageMetadata)
        );
    }

    @Override
    public CommonCommand getCommand() {
        return CommonCommand.SET_DICT;
    }
}
