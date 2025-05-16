package org.example.dictionarybot.handler.income.action.command.impl;

import javassist.compiler.Lex;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.data.MenuRowItemDto;
import org.example.dictionarybot.handler.income.action.command.CommandAction;
import org.example.dictionarybot.handler.income.action.command.CommonCommand;
import org.example.dictionarybot.neo4j.data.Lexeme;
import org.example.dictionarybot.neo4j.repo.ImplLexemeRepository;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.data.Pagination;
import org.example.dictionarybot.service.message.MessageProcessingHelper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetSynonymsCommandAction implements CommandAction {
    private final MessageProcessingHelper messageProcessingHelper;
    private final ImplLexemeRepository lexemeRepository;
    private final RedisService redisService;
    private final int DEFAULT_PAGE_SIZE = 10;

    @Override
    public Optional<AbstractMessageDto> process(
            @NonNull AbstractUserMessageDto abstractUserMessageDto,
            @NonNull String command,
            @NonNull TelegramMessageMetadata defaultMessageMetadata,
            @NonNull UserData userData) {

        Map<String, String> params =  messageProcessingHelper.getCommandParams(command);
        String lexeme = messageProcessingHelper.splitFrom(params.get(MessageProcessingHelper.LEXEME_PARAM), "#");
        String currentPageParam = params.get(MessageProcessingHelper.PAGE_PARAM);
        Pagination<Lexeme> dictionaryPagination =
                lexemeRepository.getSynonymsOfLexeme(
                        lexeme,
                        Integer.parseInt(currentPageParam),
                        DEFAULT_PAGE_SIZE);

        Set<MenuRowItemDto> items =
                dictionaryPagination.items().stream().map(
                        d -> new MenuRowItemDto(
                                CommonCommand.CURRENT_LEXEME.getCommand() + "?" + MessageProcessingHelper.LEXEME_PARAM + "=" + d.getLexeme(),
                                d.getLexeme())
                ).collect(Collectors.toSet());

        String prevCommand = redisService.getOrCreateUserData(abstractUserMessageDto.getUserId())
                .getLastChosenCommand()
                .get(CommonCommand.CURRENT_LEXEME);

        Pagination<MenuRowItemDto> page = new Pagination<>(dictionaryPagination.pageSize(), dictionaryPagination.currentPage(), dictionaryPagination.totalPages(), items.stream().toList());


        return Optional.of(
                messageProcessingHelper.getPaginationMenu(
                        abstractUserMessageDto.getChatId(),
                        CommonCommand.GET_SYNONYMS.getCommand(),
                        true,
                        "найдены синонимы",
                        messageProcessingHelper.getReturnButton(prevCommand),
                        defaultMessageMetadata,
                        page
                ));
    }

    @Override
    public CommonCommand getCommand() {
        return CommonCommand.GET_SYNONYMS;
    }
}
