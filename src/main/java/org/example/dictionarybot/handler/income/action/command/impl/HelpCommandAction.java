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
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.PropertyService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class HelpCommandAction implements CommandAction {
    private final PropertyService propertyService;

    @Override
    public Optional<AbstractMessageDto> process(@NonNull AbstractUserMessageDto abstractUserMessageDto,
                                                @NonNull String command,
                                                @NonNull TelegramMessageMetadata defaultMessageMetadata,
                                                @NonNull UserData userData) {
        return Optional.of(getHelpText(abstractUserMessageDto.getChatId(), defaultMessageMetadata));
    }

    private MessageTextDto getHelpText(String chatId, TelegramMessageMetadata defaultMessageMetadata) {
        return new MessageTextDto(
                chatId,
                propertyService.getMessageProperty("dictionary_command_help"),
                defaultMessageMetadata
        );
    }
    @Override
    public CommonCommand getCommand() {
        return CommonCommand.HELP;
    }
}
