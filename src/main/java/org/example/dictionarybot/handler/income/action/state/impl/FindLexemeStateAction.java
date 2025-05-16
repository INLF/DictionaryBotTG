package org.example.dictionarybot.handler.income.action.state.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.request.PlainTextUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.MessageMenuDto;
import org.example.dictionarybot.dto.response.menu.data.MenuRowDto;
import org.example.dictionarybot.dto.response.menu.data.MenuRowItemDto;
import org.example.dictionarybot.handler.income.action.command.CommonCommand;
import org.example.dictionarybot.handler.income.action.state.StateAction;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.neo4j.data.Lexeme;
import org.example.dictionarybot.neo4j.repo.ImplLexemeRepository;
import org.example.dictionarybot.neo4j.repo.LexemeRepository;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.PropertyService;
import org.example.dictionarybot.service.data.Pagination;
import org.example.dictionarybot.service.message.MessageProcessingHelper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindLexemeStateAction implements StateAction {
    private final LexemeRepository lexemeRepository;
    private final PropertyService propertyService;
    private final MessageProcessingHelper messageProcessingHelper;
    private final RedisService redisService;
    private final int LIMIT = 5;

    @Override
    public Optional<AbstractMessageDto> process(
            @NonNull AbstractUserMessageDto abstractUserMessageDto,
            @NonNull TelegramMessageMetadata defaultMessageMetadata,
            @NonNull UserData userData) {

        PlainTextUserMessageDto messageDto = (PlainTextUserMessageDto) abstractUserMessageDto;
        String prevCommand = redisService.getOrCreateUserData(abstractUserMessageDto.getUserId())
                .getLastChosenCommand()
                .get(CommonCommand.START);
        // Tilda '~' here stands for fuzzy search
        Set<Lexeme> foundLexemes = lexemeRepository.findAllMatchingLexemes(
                messageDto.getText() + "~",
                LIMIT
        );

        List<MenuRowDto> rows = new java.util.ArrayList<>(foundLexemes.stream()
                .map(d -> new MenuRowDto(
                        List.of(
                                new MenuRowItemDto(
                                        CommonCommand.CURRENT_LEXEME.getCommand() + "?" +
                                                MessageProcessingHelper.LEXEME_PARAM + "=" + messageProcessingHelper.concatWith(d.getLexeme(), "_"),
                                        d.getLexeme() + " - " + (
                                                d.getSenses() != null && !d.getSenses().isEmpty() ? d.getSenses().getFirst() : "—"
                                        ))
                        ))).toList());
        rows.add(new MenuRowDto(
                List.of(new MenuRowItemDto(
                        prevCommand,
                        "Назад"
                ))
        ));

        return Optional.of(
                new MessageMenuDto(
                        abstractUserMessageDto.getChatId(),
                        propertyService.getMessageProperty("found_lexemes_command_title"),
                        rows,
                        defaultMessageMetadata
                )
        );

    }

    @Override
    public UserState getUserState() {
        return UserState.FIND_LEXEME;
    }
}
