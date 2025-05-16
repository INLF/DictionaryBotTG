package org.example.dictionarybot.handler.income.action.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dictionarybot.dto.request.AbstractUserMessageDto;
import org.example.dictionarybot.dto.response.AbstractMessageDto;
import org.example.dictionarybot.dto.response.TelegramMessageMetadata;
import org.example.dictionarybot.dto.response.menu.data.MenuRowItemDto;
import org.example.dictionarybot.dto.response.text.MessageTextDto;
import org.example.dictionarybot.handler.income.action.command.CommandAction;
import org.example.dictionarybot.handler.income.action.command.CommonCommand;
import org.example.dictionarybot.handler.income.action.state.UserState;
import org.example.dictionarybot.neo4j.data.Lexeme;
import org.example.dictionarybot.neo4j.repo.ImplLexemeRepository;
import org.example.dictionarybot.redis.RedisService;
import org.example.dictionarybot.redis.data.UserData;
import org.example.dictionarybot.service.PropertyService;
import org.example.dictionarybot.service.data.Pagination;
import org.example.dictionarybot.service.message.MessageProcessingHelper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetTranslationsCommandAction implements CommandAction {
    private final MessageProcessingHelper messageProcessingHelper;
    private final PropertyService propertyService;
    private final ImplLexemeRepository lexemeRepository;
    private final RedisService redisService;

    private final int DEFAULT_PAGE_SIZE = 5;
    @Override
    public Optional<AbstractMessageDto> process(@NonNull AbstractUserMessageDto abstractUserMessageDto,
                                                @NonNull String command,
                                                @NonNull TelegramMessageMetadata defaultMessageMetadata,
                                                @NonNull UserData userData) {
        Map<String, String> params =  messageProcessingHelper.getCommandParams(command);
        String lexeme = messageProcessingHelper.splitFrom(params.get(MessageProcessingHelper.LEXEME_PARAM), "_");
        String currentPageParam = params.get(MessageProcessingHelper.PAGE_PARAM);
        Pagination<Lexeme> dictionaryPagination =
                lexemeRepository.getTranslationsOfLexeme(
                        lexeme,
                        Integer.parseInt(currentPageParam),
                        DEFAULT_PAGE_SIZE);

        Set<MenuRowItemDto> items =
                dictionaryPagination.items().stream().map(
                        d -> new MenuRowItemDto(
                                CommonCommand.CURRENT_LEXEME.getCommand() + "?" + MessageProcessingHelper.LEXEME_PARAM + "=" + messageProcessingHelper.concatWith(d.getLexeme(), "_"),
                                d.getLexeme())
                ).collect(Collectors.toSet());

        String prevCommand = redisService.getOrCreateUserData(abstractUserMessageDto.getUserId())
                .getLastChosenCommand()
                .get(CommonCommand.CURRENT_LEXEME);

        Pagination<MenuRowItemDto> page = new Pagination<>(dictionaryPagination.pageSize(), dictionaryPagination.currentPage(), dictionaryPagination.totalPages(), items.stream().toList());

        log.info("Get translations for {}", items.size());
        return Optional.of(
                messageProcessingHelper.getPaginationMenu(
                        abstractUserMessageDto.getChatId(),
                        CommonCommand.GET_TRANSLATIONS.getCommand() + "?" + MessageProcessingHelper.LEXEME_PARAM + "=" + lexeme,
                        true,
                        "Найдены переводы",
                        messageProcessingHelper.getReturnButton(prevCommand),
                        defaultMessageMetadata,
                        page
                ));
    }


    @Override
    public CommonCommand getCommand() {
        return CommonCommand.GET_TRANSLATIONS;
    }
}
