package org.example.dictionarybot.handler.income.action.command.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.example.dictionarybot.neo4j.data.Lexeme;
import org.example.dictionarybot.neo4j.repo.ImplLexemeRepository;
import org.example.dictionarybot.neo4j.repo.LexemeRepository;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.message.MessageProcessingHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetLexemeSensesCommandAction implements CommandAction {
    private final MessageProcessingHelper messageProcessingHelper;
    private final LexemeRepository lexemeRepository;
    private final RedisService redisService;

    @Override
    public Optional<AbstractMessageDto> process(
            @NonNull AbstractUserMessageDto abstractUserMessageDto,
            @NonNull String command,
            @NonNull TelegramMessageMetadata defaultMessageMetadata,
            @NonNull UserData userData) {

        userData.setUserState(UserState.GET_CURRENT_LEXEME_SENSES);
        redisService.saveUserData(userData, abstractUserMessageDto.getUserId());

        Map<String, String> params = messageProcessingHelper.getCommandParams(command);
        String lexemeParam = messageProcessingHelper.splitFrom(params.get(MessageProcessingHelper.LEXEME_PARAM), "_");

        String prevCommand = redisService.getOrCreateUserData(abstractUserMessageDto.getUserId())
                .getLastChosenCommand()
                .get(CommonCommand.CURRENT_LEXEME);

        Lexeme foundLexeme = lexemeRepository.findByLexeme(lexemeParam).get();


        return Optional.of(
                getSensesText(
                        foundLexeme.getSenses(),
                        abstractUserMessageDto.getChatId(),
                        prevCommand,
                        defaultMessageMetadata
                ));
    }

    private MessageMenuDto getSensesText(
            List<String> senses,
            String chatId,
            String prevCommand,
            TelegramMessageMetadata defaultMessageMetadata) {
        return new MessageMenuDto(
                chatId,
                senses.stream().reduce((s1 , s2) -> s1.concat("\n").concat(s2)).orElse(""),
                List.of(messageProcessingHelper.getReturnButton(prevCommand)),
                defaultMessageMetadata
        );
    }

    @Override
    public CommonCommand getCommand() {
        return CommonCommand.GET_LEXEME_SENSES;
    }
}
