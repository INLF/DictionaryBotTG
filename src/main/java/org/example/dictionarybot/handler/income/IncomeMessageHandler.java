package org.example.dictionarybot.handler.income;

import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.client.BotMessageClient;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.request.CallBackQueryUserMessageDto;
import org.example.dictionarybot.dto.request.CommandUserMessageDto;
import org.example.dictionarybot.dto.request.UserMessageType;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.MessageMenuDto;
import org.example.dictionarybot.handler.income.action.command.CommandAction;
import org.example.dictionarybot.handler.income.action.command.CommonCommand;
import org.example.dictionarybot.handler.income.action.state.StateAction;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.message.MessageProcessingHelper;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Component
public class IncomeMessageHandler {
    private final BotMessageClient botMessageClient;
    private final RedisService redisService;
    private final Map<CommonCommand, CommandAction> commandActions;
    private final Map<UserState, StateAction> stateActions;
    private final MessageProcessingHelper messageProcessingHelper;


    public IncomeMessageHandler(BotMessageClient botMessageClient, RedisService redisService,
                                List<CommandAction> commandActions, List<StateAction> stateActions,
                                MessageProcessingHelper messageProcessingHelper) {
        this.botMessageClient = botMessageClient;
        this.redisService = redisService;
        this.commandActions = commandActions.stream().collect(Collectors.toMap(CommandAction::getCommand, Function.identity()));
        this.stateActions = stateActions.stream().collect(Collectors.toMap(StateAction::getUserState, Function.identity()));
        this.messageProcessingHelper = messageProcessingHelper;
    }

    public void handle(AbstractUserMessageDto abstractUserMessageDto) {
        log.info("Received AbstractUserMessageDto: {}", abstractUserMessageDto);
        UserData userData = redisService.getOrCreateUserData(abstractUserMessageDto.getUserId());
        try {
            String editableMessageId = redisService.getEditableMessageId(abstractUserMessageDto.getUserId());
            // process CommandAction/CallBackQuery otherwise it's StateAction
            // if it's callBackData check if it's containing command

            if (abstractUserMessageDto.getMessageType() == UserMessageType.COMMAND) {
                CommandUserMessageDto commandUserMessageDto = (CommandUserMessageDto) abstractUserMessageDto;
                processCommand(commandUserMessageDto, commandUserMessageDto.getCommand(), editableMessageId, userData);
            } else if (abstractUserMessageDto.getMessageType() == UserMessageType.CALLBACK_QUERY) {
                CallBackQueryUserMessageDto callbackQueryUserMessageDto = (CallBackQueryUserMessageDto) abstractUserMessageDto;
                processCallbackQuery(callbackQueryUserMessageDto, editableMessageId, userData);
            } else {
                processUserState(abstractUserMessageDto, editableMessageId, userData);
            }

        } catch (Exception e) {
            log.error("Error during handle msg", e);
            processError(abstractUserMessageDto);
        }
    }


    private void processUserState(AbstractUserMessageDto abstractMessageDto, String editMsgId, UserData userData) {
        TelegramMessageMetadata defaultMessageMetadata = messageProcessingHelper.getDefaultMessageMetadata(
                editMsgId,
                null
        );
        stateActions.get(userData.getUserState())
                .process(abstractMessageDto, defaultMessageMetadata, userData)
                .ifPresent(botMessageClient::sendOutcomeMessage);
    }


    private void processCallbackQuery(CallBackQueryUserMessageDto callbackQueryDto, String editMsgId, UserData userData) {
        Optional<String> maybeCommand = messageProcessingHelper.getCommand(callbackQueryDto.getCallBackQueryData());
        if (maybeCommand.isPresent()) {
            processCommand(callbackQueryDto, maybeCommand.get(), editMsgId, userData);
        } else {
            processUserState(callbackQueryDto, editMsgId, userData);
        }
    }

    private void processCommand(AbstractUserMessageDto messageDto, String command, String editMsgId, UserData userData) {
        TelegramMessageMetadata defaultMessageMetadata = messageProcessingHelper.getDefaultMessageMetadata(
                editMsgId,
                messageProcessingHelper.getPath(command)
        );

        Optional<CommonCommand> commandEnum = CommonCommand.getByCommand(messageProcessingHelper.getPath(command));

        if (commandEnum.isPresent()) {
            commandActions.get(commandEnum.get())
                    .process(messageDto, command, defaultMessageMetadata, userData)
                    .ifPresent(botMessageClient::sendOutcomeMessage);
        } else {
            MessageMenuDto commandNotRecognizedMsg = messageProcessingHelper.getCommandNotRecognizedMsg(messageDto.getChatId(), editMsgId);
            botMessageClient.sendOutcomeMessage(commandNotRecognizedMsg);
        }

    }

    private void processError(AbstractUserMessageDto abstractUserMessageDto) {
        redisService.saveUserData(UserData.builder().userState(UserState.START).build(), abstractUserMessageDto.getUserId());
        AbstractMessageDto errorMessage = messageProcessingHelper.getErrorMessage(
                abstractUserMessageDto.getUserId(),
                messageProcessingHelper.getDefaultMessageMetadata(null, null)
        );
        botMessageClient.sendOutcomeMessage(errorMessage);
    }

}
