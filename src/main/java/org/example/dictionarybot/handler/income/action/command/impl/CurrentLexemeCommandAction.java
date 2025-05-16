package org.example.dictionarybot.handler.income.action.command.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.MessageMenuDto;
import org.example.dictionarybot.dto.response.text.MessageTextDto;
import org.example.dictionarybot.handler.income.action.command.CommandAction;
import org.example.dictionarybot.handler.income.action.command.CommonCommand;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.neo4j.data.Lexeme;
import org.example.dictionarybot.neo4j.repo.LexemeRepository;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.PropertyService;
import org.example.dictionarybot.service.message.MessageProcessingHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrentLexemeCommandAction implements CommandAction {
    private final MessageProcessingHelper messageProcessingHelper;
    private final LexemeRepository lexemeRepository;
    private final RedisService redisService;
    private final PropertyService propertyService;
    @Override
    public Optional<AbstractMessageDto> process(
            @NonNull AbstractUserMessageDto abstractUserMessageDto,
            @NonNull String command,
            @NonNull TelegramMessageMetadata defaultMessageMetadata,
            @NonNull UserData userData) {

        userData.getLastChosenCommand().put(getCommand(), command);
        userData.setUserState(UserState.CURRENT_LEXEME);
        redisService.saveUserData(userData, abstractUserMessageDto.getUserId());

        Map<String, String> params =  messageProcessingHelper.getCommandParams(command);
        String lexeme = messageProcessingHelper.splitFrom(params.get(MessageProcessingHelper.LEXEME_PARAM), "_");
        log.info("Lexeme is : {}", lexeme);
        Lexeme foundLexeme = lexemeRepository.findByLexeme(lexeme).get();


        return Optional.of(
                getLexemeMenuMessage(
                        foundLexeme,
                        abstractUserMessageDto,
                        defaultMessageMetadata));
    }

    private MessageMenuDto getLexemeMenuMessage(Lexeme lexeme,
                                                AbstractUserMessageDto defaultUserMessageDto,
                                                TelegramMessageMetadata defaultMessageMetadata) {
        return new MessageMenuDto(
                defaultUserMessageDto.getUserId(),
                lexeme.getLexeme(),
                List.of(
                        messageProcessingHelper.getLexemeSensesButtonRow(messageProcessingHelper.concatWith(lexeme.getLexeme(), "_")),
                        messageProcessingHelper.getLexemeTranslationsButtonRow(messageProcessingHelper.concatWith(lexeme.getLexeme(), "_")),
                        messageProcessingHelper.getReturnButtonRow()
                ),
                defaultMessageMetadata);
    }

    @Override
    public CommonCommand getCommand() {
        return CommonCommand.CURRENT_LEXEME;
    }
}
