package org.example.dictionarybot.handler.income.action.command.impl;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.MessageMenuDto;
import org.example.dictionarybot.dto.response.menu.data.MenuRowDto;
import org.example.dictionarybot.dto.response.menu.data.MenuRowItemDto;
import org.example.dictionarybot.handler.income.action.command.CommandAction;
import org.example.dictionarybot.handler.income.action.command.CommonCommand;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.neo4j.service.DictionaryService;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.PropertyService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DictionaryEditorCommandAction implements CommandAction {
    private final DictionaryService dictionaryService;
    private final RedisService redisService;
    private final PropertyService propertyService;

    @Override
    public Optional<AbstractMessageDto> process(@NonNull AbstractUserMessageDto abstractUserMessageDto,
                                                @NonNull String command,
                                                @NonNull TelegramMessageMetadata defaultMessageMetadata,
                                                @NonNull UserData userData) {
        userData.setUserState(UserState.EDIT_DICTIONARY);
        redisService.saveUserData(userData, abstractUserMessageDto.getUserId());
        MessageMenuDto menu = new MessageMenuDto(
                abstractUserMessageDto.getChatId(),
                propertyService.getMessageProperty("dictionary_editor_menu"),
                List.of(
                        new MenuRowDto(List.of(new MenuRowItemDto(
                                CommonCommand.MY_DICTS.getCommand() + "?p=0",
                                propertyService.getMessageProperty("my_dictionaries_button_text"))))
                ),
                defaultMessageMetadata
                );

        return Optional.of(menu);
    }

    @Override
    public CommonCommand getCommand() {
        return CommonCommand.DICTIONARY_EDITOR;
    }
}
