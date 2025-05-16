package org.example.dictionarybot.handler.income.action.command.impl;

import lombok.RequiredArgsConstructor;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.MessageMenuDto;
import org.example.dictionarybot.dto.response.menu.data.MenuRowDto;
import org.example.dictionarybot.dto.response.menu.data.MenuRowItemDto;
import org.example.dictionarybot.dto.response.text.MessageTextDto;
import org.example.dictionarybot.handler.income.action.command.CommandAction;
import org.example.dictionarybot.handler.income.action.command.CommonCommand;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.PropertyService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindLexemeCommandAction implements CommandAction {
    private final RedisService redisService;
    private final PropertyService propertyService;
    @Override
    public Optional<AbstractMessageDto> process(@NonNull AbstractUserMessageDto abstractUserMessageDto,
                                                @NonNull String command,
                                                @NonNull TelegramMessageMetadata defaultMessageMetadata,
                                                @NonNull UserData userData) {
        userData.setUserState(UserState.FIND_LEXEME);
        userData.getLastChosenCommand().put(getCommand(), command);
        redisService.saveUserData(userData, abstractUserMessageDto.getUserId());

        return Optional.of(getText(abstractUserMessageDto.getChatId(), defaultMessageMetadata));
    }

    private MessageTextDto getText(String chatId, TelegramMessageMetadata defaultMessageMetadata) {
        return new MessageTextDto(
                chatId,
                propertyService.getMessageProperty("lexeme_find_command"),
                defaultMessageMetadata
        );
    }

    @Override
    public CommonCommand getCommand() {
        return CommonCommand.FIND_MATCHING_LEXEMES;
    }
}
