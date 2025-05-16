package org.example.dictionarybot.handler.income.action.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.request.CommandUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.MessageMenuDto;
import org.example.dictionarybot.dto.response.webapp.WebAppInfoDto;
import org.example.dictionarybot.dto.response.webapp.WebAppLinkDto;
import org.example.dictionarybot.handler.income.action.command.CommandAction;
import org.example.dictionarybot.handler.income.action.command.CommonCommand;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.PropertyService;
import org.example.dictionarybot.service.message.MessageProcessingHelper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartCommandAction implements CommandAction {
    private final RedisService redisService;
    private final PropertyService propertyService;
    private final MessageProcessingHelper messageProcessingHelper;

    @Override
    public Optional<AbstractMessageDto> process(@NonNull AbstractUserMessageDto defaultUserMessageDto,
                                                @NonNull String command,
                                                @NonNull TelegramMessageMetadata defaultMessageMetadata,
                                                @NonNull UserData userData) {
        userData.setUserState(UserState.START);
        userData.getLastChosenCommand().put(getCommand(), command);
        redisService.saveUserData(userData, defaultUserMessageDto.getUserId());

        return Optional.of(getStartMessage(defaultUserMessageDto, defaultMessageMetadata));
    }

    private MessageMenuDto getStartMessage(AbstractUserMessageDto defaultUserMessageDto, TelegramMessageMetadata defaultMessageMetadata) {
        return new MessageMenuDto(
                defaultUserMessageDto.getUserId(),
                propertyService.getMessageProperty("start_text"),
                List.of(
                        messageProcessingHelper.getLexemeSearchButtonRow(),
                        messageProcessingHelper.getHelpButtonRow(),
                        messageProcessingHelper.getReturnButtonRow()
                        ),
                defaultMessageMetadata);
    }



//    private WebAppLinkDto getStartMessage(AbstractUserMessageDto defaultUserMessageDto, TelegramMessageMetadata defaultMessageMetadata) {
//        return new WebAppLinkDto(
//                defaultUserMessageDto.getChatId(),
//                propertyService.getMessageProperty("start_text"),
//                defaultMessageMetadata,
//                new WebAppInfoDto("https://bf7f-195-85-250-66.ngrok-free.app/app"),
//                "s");
//    }

    @Override
    public CommonCommand getCommand() {
        return CommonCommand.START;
    }
}
